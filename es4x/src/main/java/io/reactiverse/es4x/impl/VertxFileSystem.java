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

  private static final Pattern DOT_SLASH = Pattern.compile("[^.]\\." + File.separator);
  private static final List<String> EXTENSIONS = Arrays.asList(".mjs", ".js");
  private static final Path EMPTY = Paths.get("");
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

    // all paths are unix paths
    cwd = cwd.replace('\\', '/');
    // ensure it ends with /
    if (cwd.charAt(cwd.length() - 1) != '/') {
      cwd += '/';
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

  // keep track of the well-known roots
  private final File cwd;
  private final File cache;
  private final File download;
  private final File baseUrl;

  public VertxFileSystem(final Vertx vertx) {
    Objects.requireNonNull(vertx, "vertx must be non null.");
    this.vertx = (VertxInternal) vertx;
    // resolve the well known roots
    this.cwd = new File(getCWD());
    this.cache = this.vertx.resolveFile("");
    this.baseUrl = new File(this.cwd, System.getProperty("baseUrl", "node_modules"));
    this.download = new File(this.baseUrl, ".download");
  }

  private File resolveFile(File file) throws IOException {
    if (file.isFile()) {
      return file.getCanonicalFile();
    }

    // keep a reference as we will use it in a loop
    final String path = file.getPath();

    for (String ext : EXTENSIONS) {
      file = vertx.resolveFile(path + ext);

      if (file.isFile()) {
        return file.getCanonicalFile();
      }
    }

    if (file.isDirectory()) {
      return resolveFile(new File(file, "index"));
    }

    return file;
  }

  @Override
  public Path parsePath(URI uri) {
    switch (uri.getScheme()) {
      case "http":
      case "https":
        try {
          // compute hash
          String source = uri.getScheme() + "://" + uri.getAuthority();
          String hash = md5(source);
          // save
          urlMap.put(hash, source);
          File target = new File(download, hash + File.separator + uri.getPath());

          if (uri.getQuery() != null) {
            LOGGER.warn("URI with query will always force a download");
            download(uri.toURL(), target);
          } else {
            if (!target.exists()) {
              download(uri.toURL(), target);
            }
          }
          // relativize back to CWD to allow better interop with vert.x file system
          return Paths.get(target.getPath().substring(cwd.getPath().length() + 1));
        } catch (IOException | NoSuchAlgorithmException e) {
          throw new InvalidPathException(uri.toString(), e.getMessage());
        }
      default:
        throw new UnsupportedOperationException("unsupported scheme: " + uri.getScheme());
    }
  }

  @Override
  public Path parsePath(String path) {
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
    if (file.getPath().startsWith(cache.getPath())) {
      if (file.equals(cache)) {
        file = cache;
      } else {
        file = new File(cwd, file.getPath().substring(cache.getPath().length()));
      }
    }
    // if it's a download, get the file to the download dir
    if (file.getPath().startsWith(download.getPath())) {
      // download if missing
      if (!file.exists()) {
        // build an URL from the path
        String target = file.getPath().substring(download.getPath().length() + 1);
        int split = target.indexOf(File.separator);
        String source = target.substring(0, split);
        if (!urlMap.containsKey(source)) {
          throw new InvalidPathException(path, "Cannot resolve the source of the hash: " + source);
        }
        try {
          download(new URL(source + target.substring(split)), file);
        } catch (IOException e) {
          throw new InvalidPathException(path, e.getMessage());
        }
      }
    }
    // relativize back to CWD to allow better interop with vert.x file system
    if (file.getPath().startsWith(cwd.getPath())) {
      if (file.equals(cwd)) {
        System.out.println(path + " --> " + EMPTY);
        return EMPTY;
      } else {
        System.out.println(path + " --> " + Paths.get(file.getPath().substring(cwd.getPath().length() + 1)));
        return Paths.get(file.getPath().substring(cwd.getPath().length() + 1));
      }
    }

    System.out.println(path + " --> " + file.toPath());
    return file.toPath();
  }

  private void download(URL url, File target) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setInstanceFollowRedirects(true);
    conn.setRequestProperty("User-Agent", "es4x/0.12.0");

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
    if (isFollowLinks(linkOptions)) {
      DELEGATE.checkAccess(resolve(path), modes.toArray(new AccessMode[0]));
    } else if (modes.isEmpty()) {
      DELEGATE.readAttributes(resolve(path), "isRegularFile", LinkOption.NOFOLLOW_LINKS);
    } else {
      throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
    }
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    DELEGATE.createDirectory(resolve(dir), attrs);
  }

  @Override
  public void delete(Path path) throws IOException {
    DELEGATE.delete(resolve(path));
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    DELEGATE.copy(resolve(source), resolve(target), options);
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    DELEGATE.move(resolve(source), resolve(target), options);
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    File resolved = vertx.resolveFile(path.toString());
    if (resolved == null) {
      throw new InvalidPathException(path.toString(), "Cannot resolve the path");
    }
    if (resolved.isDirectory()) {
      System.out.println("@@ going inside dir: " + resolved);
      resolved = resolveFile(new File(resolved, "index"));
    }
    if (resolved == null) {
      throw new InvalidPathException(path.toString(), "Cannot resolve the path");
    }
    return DELEGATE.newByteChannel(resolved.toPath(), options, attrs);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    return DELEGATE.newDirectoryStream(resolve(dir), filter);
  }

  @Override
  public void createLink(Path link, Path existing) throws IOException {
    DELEGATE.createLink(resolve(link), resolve(existing));
  }

  @Override
  public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
    DELEGATE.createSymbolicLink(resolve(link), resolve(target), attrs);
  }

  @Override
  public Path readSymbolicLink(Path link) throws IOException {
    return DELEGATE.readSymbolicLink(resolve(link));
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    return DELEGATE.readAttributes(resolve(path), attributes, options);
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
    DELEGATE.setAttribute(resolve(path), attribute, value, options);
  }

  @Override
  public Path toAbsolutePath(Path path) {
    return resolve(path).toAbsolutePath();
  }

  @Override
  public void setCurrentWorkingDirectory(Path currentWorkingDirectory) {
    throw new IllegalStateException("Changing Vert.x Current Working Directory is not allowed after startup.");
  }

  @Override
  public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
    if (path.isAbsolute()) {
      // this path has been resolved, before
      return path;
    }
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    File resolved = vertx.resolveFile(path.toString());
    if (resolved.exists()) {
      return resolved.toPath()
        .toRealPath(linkOptions);
    }
    // attempt to fallback with some basic resolution add missing extension, directory root -> index
    return resolveFile(resolved)
      .toPath()
      .toRealPath(linkOptions);
  }

  private Path resolve(Path path) {
    if (path.isAbsolute()) {
      // this path has been resolved, before
      return path;
    }
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    return vertx.resolveFile(path.toString())
      .toPath();
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
