/*
 * Copyright 2018 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */
package io.reactiverse.es4x.impl;

import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.graalvm.polyglot.io.FileSystem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class VertxFileSystem implements FileSystem {

  private static final Logger LOGGER = LoggerFactory.getLogger(VertxFileSystem.class);

  private static final Pattern DOT_SLASH = Pattern.compile("^\\." + File.separator + "|" + File.separator + "\\." + File.separator);
  private static final FileSystemProvider DELEGATE = FileSystems.getDefault().provider();

  private static String md5(String input) throws NoSuchAlgorithmException {
    // Create MessageDigest instance for MD5
    MessageDigest md = MessageDigest.getInstance("MD5");
    //Add password bytes to digest
    md.update(input.getBytes(StandardCharsets.UTF_8));
    //Get the hash's bytes
    byte[] bytes = md.digest();
    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
    }
    //Get complete hashed input in hex format
    return sb.toString();
  }

  public static String getCWD() {
    // clean up the current working dir
    String cwdOverride = System.getProperty("vertx.cwd");
    String cwd;
    // are the any overrides?
    if (cwdOverride != null) {
      cwd = new File(cwdOverride).getAbsolutePath();
    } else {
      // ensure it's not null
      cwd = System.getProperty("user.dir", "");
    }

    // ensure it ends with /
    if (cwd.charAt(cwd.length() - 1) != File.separatorChar) {
      cwd += File.separatorChar;
    }

    return cwd;
  }

  /**
   * A relative import is one that starts with {@code /}, {@code ./} or {@code ../}. Some examples include:
   *
   * <ul>
   *   <li>{@code import Entry from "./components/Entry";}</li>
   *   <li>{@code import { DefaultHeaders } from "../constants/http";}</li>
   *   <li>{@code import "/mod";}</li>
   * </ul>
   */
  private static boolean isRelativeImport(String path) {
    int len = path.length();
    if (len > 0) {
      if (path.charAt(0) == '/') {
        return true;
      } else if (len > 1) {
        if (path.charAt(0) == '.' && path.charAt(1) == '/') {
          return true;
        } else if (len > 2) {
          return path.charAt(0) == '.' && path.charAt(1) == '.' && path.charAt(2) == '/';
        }
      }
    }

    return false;
  }

  private final Map<String, String> urlMap = new ConcurrentHashMap<>();
  private final VertxInternal vertx;

  private final String[] extensions;
  // keep track of the well-known roots
  private final String cwd;
  private final String cachedir;
  private final String downloaddir;
  private final String baseUrl;

  public VertxFileSystem(final Vertx vertx, String... extensions) {
    this.vertx = (VertxInternal) vertx;
    this.extensions = extensions;
    // resolve the well known roots
    this.cwd = getCWD();
    this.cachedir = this.vertx.resolveFile("").getPath() + File.separator;
    this.baseUrl = new File(this.cwd, System.getProperty("baseUrl", "node_modules")).getPath() + File.separator;
    this.downloaddir = new File(this.baseUrl, ".download").getPath() + File.separator;
  }

  private File resolveFile(File root, String suffix) {
    File file = suffix == null ? root : new File(root, suffix);

    if (file.isFile()) {
      return file;
    }

    // keep a reference as we will use it in a loop
    if (extensions != null) {
      final String path = file.getPath();

      for (String ext : extensions) {
        if (path.endsWith(ext)) {
          // skip file already ending with the target extension
          continue;
        }

        File f = vertx.resolveFile(path + ext);

        if (f.isFile()) {
          return f;
        }
      }
    }

    if (file.isDirectory()) {
      return resolveFile(file, "index");
    }

    return root;
  }

  @Override
  public Path parsePath(URI uri) {
    LOGGER.trace("parsePath(URI)");
    switch (uri.getScheme()) {
      case "file":
        return parsePath(uri.getPath());
      case "http":
      case "https":
        try {
          // compute hash
          String source = uri.getScheme() + "://" + uri.getAuthority();
          String hash = md5(source);
          // save
          urlMap.put(hash, source);
          File target = new File(downloaddir, hash + File.separator + uri.getPath());

          if (uri.getQuery() != null) {
            LOGGER.warn("URI with query will always force a download");
            download(uri.toURL(), target);
          } else {
            if (!target.exists()) {
              download(uri.toURL(), target);
            }
          }
          // the newly saved file
          assert target.isAbsolute() : "path should be absolute";
          return target.toPath();
        } catch (IOException | NoSuchAlgorithmException e) {
          throw new InvalidPathException(uri.toString(), e.getMessage());
        }
      default:
        throw new UnsupportedOperationException("unsupported scheme: " + uri.getScheme());
    }
  }

  /**
   * Given a path string returns a absolute path relative to the CWD
   */
  @Override
  public Path parsePath(String path) {
    LOGGER.trace(String.format("parsePath(%s)", path));
    File file;
    // relativize the path
    if (!isRelativeImport(path)) {
      file = new File(baseUrl, path);
    } else {
      file = new File(path);
    }
    // make absolute
    if (!file.isAbsolute()) {
      file = new File(cwd, file.getPath());
    }
    // simple normalize
    file = new File(DOT_SLASH.matcher(file.getPath()).replaceAll(File.separator));
    // aliasing from cache back to CWD
    if (file.getPath().startsWith(cachedir)) {
      file = new File(cwd, file.getPath().substring(cachedir.length()));
    }
    // if it's a download, get the file to the download dir
    if (file.getPath().startsWith(downloaddir)) {
      // download if missing
      if (!file.exists()) {
        // build an URL from the path
        String target = file.getPath().substring(downloaddir.length());
        int split = target.indexOf(File.separator);
        String source = target.substring(0, split);
        // can we map the hash to a url?
        if (!urlMap.containsKey(source)) {
          throw new InvalidPathException(path, "Cannot resolve the source of the hash: " + source);
        }
        try {
          // try to download
          download(new URL(urlMap.get(source) + target.substring(split)), file);
        } catch (IOException e) {
          throw new InvalidPathException(path, e.getMessage());
        }
      }
    }
    // if a paths starts with cwd -> strip + force resolve through vertx
    if (file.getPath().startsWith(cwd)) {
      String stripped = file.getPath().substring(cwd.length());
      // force all resolutions to go over vertx file resolver to allow
      // getting the right path objects even if on the classpath
      file = vertx.resolveFile(stripped);
      // aliasing from cache back to CWD
      if (file.getPath().startsWith(cachedir)) {
        file = new File(cwd, file.getPath().substring(cachedir.length()));
      }
      // make absolute
      if (!file.isAbsolute()) {
        file = new File(cwd, file.getPath());
      }
    }
    // assure the format is right
    assert file.isAbsolute() : "path should be absolute";
    return file.toPath();
  }

  private void download(URL url, File target) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setInstanceFollowRedirects(true);
    conn.setRequestProperty("User-Agent", "es4x/pm");

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException(conn.getResponseMessage());
    }

    try (InputStream inputStream = conn.getInputStream()) {
      try (BufferedInputStream reader = new BufferedInputStream(inputStream)) {
        target.getParentFile().mkdirs();
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(target))) {
          byte[] buffer = new byte[4096];
          int bytesRead;
          while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, bytesRead);
          }
        }
      }
    }
  }

  @Override
  public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
    LOGGER.trace(String.format("checkAccess(%s)", path));
    if (isFollowLinks(linkOptions)) {
      DELEGATE.checkAccess(path, modes.toArray(new AccessMode[0]));
    } else if (modes.isEmpty()) {
      DELEGATE.readAttributes(path, "isRegularFile", LinkOption.NOFOLLOW_LINKS);
    } else {
      throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
    }
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    LOGGER.trace(String.format("createDirectory(%s)", dir));
    DELEGATE.createDirectory(dir, attrs);
  }

  @Override
  public void delete(Path path) throws IOException {
    LOGGER.trace(String.format("delete(%s)", path));
    DELEGATE.delete(path);
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    LOGGER.trace(String.format("copy(%s, %s)", source, target));
    DELEGATE.copy(source, target, options);
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    LOGGER.trace(String.format("move(%s, %s)", source, target));
    DELEGATE.move(source, target, options);
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    LOGGER.trace(String.format("newByteChannel(%s)", path));
    // if file is a directory, we try to guess it it's an index
    File file = path.toFile();

    if (file.isDirectory()) {
      file = resolveFile(file, "index");
    }

    return DELEGATE.newByteChannel(file.toPath(), options, attrs);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    LOGGER.trace(String.format("newDirectoryStream(%s)", dir));
    return DELEGATE.newDirectoryStream(dir, filter);
  }

  @Override
  public void createLink(Path link, Path existing) throws IOException {
    LOGGER.trace(String.format("createLink(%s)", link));
    DELEGATE.createLink(link, existing);
  }

  @Override
  public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
    LOGGER.trace(String.format("createSymbolicLink(%s)", link));
    DELEGATE.createSymbolicLink(link, target, attrs);
  }

  @Override
  public Path readSymbolicLink(Path link) throws IOException {
    LOGGER.trace(String.format("readSymbolicLink(%s)", link));
    return DELEGATE.readSymbolicLink(link);
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    LOGGER.trace(String.format("readAttributes(%s)", path));
    return DELEGATE.readAttributes(path, attributes, options);
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
    LOGGER.trace(String.format("setAttribute(%s)", path));
    DELEGATE.setAttribute(path, attribute, value, options);
  }

  @Override
  public Path toAbsolutePath(Path path) {
    LOGGER.trace(String.format("toAbsolutePath(%s)", path));
    return path.toAbsolutePath();
  }

  @Override
  public void setCurrentWorkingDirectory(Path currentWorkingDirectory) {
    throw new IllegalStateException("Changing Vert.x Current Working Directory is not allowed after startup.");
  }

  @Override
  public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
    LOGGER.trace(String.format("toRealPath(%s)", path));
    String name = path.toString();
    File file;
    // normalize to CWD
    if (name.startsWith(cwd)) {
      // get the real path
      file = vertx.resolveFile(name.substring(cwd.length()));
    } else {
      // use it as is
      file = path.toFile();
    }

    // if file doesn't exist, we try to guess it it's missing the extension or is an index
    // if it still fails, it fallbacks to the original file
    if (!file.exists()) {
      file = resolveFile(file, null);
    }

    return file
      .toPath()
      .toRealPath(linkOptions);
  }

  private static boolean isFollowLinks(final LinkOption... linkOptions) {
    for (LinkOption lo : linkOptions) {
      if (lo == LinkOption.NOFOLLOW_LINKS) {
        return false;
      }
    }
    return true;
  }
}
