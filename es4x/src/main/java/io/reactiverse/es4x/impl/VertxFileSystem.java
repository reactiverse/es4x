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
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import org.graalvm.polyglot.io.FileSystem;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.reactiverse.es4x.impl.Utils.*;

public final class VertxFileSystem implements FileSystem {

  private static final Logger LOGGER = LoggerFactory.getLogger(VertxFileSystem.class);

  private static final FileSystemProvider DELEGATE = FileSystems.getDefault().provider();

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

  private final Map<String, String> urlMap = new ConcurrentHashMap<>();
  private final VertxInternal vertx;

  private final String[] extensions;
  // keep track of the well-known roots
  private final String cwd;
  private final String downloadDir;
  private final String baseDir;

  private final ImportMapper mapper;

  public VertxFileSystem(final Vertx vertx, String importMap, String... extensions) {
    this.vertx = (VertxInternal) vertx;

    this.extensions = extensions;
    // resolve the well known roots
    try {
      cwd = getCWD();
      URI cwdUrl = fileToURI(new File(this.cwd).getCanonicalFile());

      if (importMap == null) {
        mapper = new ImportMapper(
          new JsonObject()
            .put("imports", new JsonObject()
              .put(toNixPath(cwd), "./")),
          cwdUrl);
        baseDir = new File(this.cwd, "node_modules").getCanonicalPath() + File.separator;
        downloadDir = new File(this.baseDir, ".download").getCanonicalPath() + File.separator;
      } else {
        baseDir = new File(this.cwd).getCanonicalPath() + File.separator;
        downloadDir = new File(this.baseDir, ".download").getCanonicalPath() + File.separator;
        JsonObject json = new JsonObject(vertx.fileSystem().readFileBlocking(importMap));
        if (json.containsKey("imports")) {
          json
            .getJsonObject("imports")
            .put(toNixPath(cwd), "./");
        } else {
          json
            .put("imports", new JsonObject()
              .put(toNixPath(cwd), "./"));
        }
        mapper = new ImportMapper(json, cwdUrl);
      }
    } catch (IOException | IllegalArgumentException e) {
      throw new IllegalArgumentException("Cannot resolve the CWD", e);
    }
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

  private Path parsePath(URL url) {
    try {
      // compute hash
      String source = url.getProtocol() + "://" + url.getAuthority();
      String hash = md5(source);
      // save
      urlMap.put(hash, source);
      File target = new File(downloadDir, hash + File.separator + url.getPath());

      if (url.getQuery() != null) {
        LOGGER.warn("URI with query will always force a download");
        downloadTo(url, target);
      } else {
        if (!target.exists()) {
          downloadTo(url, target);
        }
      }
      // the newly saved file
      assert target.isAbsolute() : "path should be absolute";
      return target.toPath();
    } catch (IOException e) {
      throw new InvalidPathException(url.toString(), e.getMessage());
    }
  }

  @Override
  public Path parsePath(URI uri) {
    LOGGER.trace(String.format("parsePath(%s)", uri));
    switch (uri.getScheme()) {
      case "file":
        return parsePath(uri.getPath());
      case "http":
      case "https":
        try {
          return parsePath(uri.toURL());
        } catch (MalformedURLException e) {
          throw new InvalidPathException(uri.toString(), e.getMessage());
        }
      default:
        throw new IllegalArgumentException("unsupported scheme: " + uri.getScheme());
    }
  }

  /**
   * Given a path string returns a absolute path relative to the CWD
   */
  @Override
  public Path parsePath(String path) {
    LOGGER.trace(String.format("parsePath(%s)", path));

    if ("".equals(path)) {
      return new File(cwd).toPath();
    }

    File file;
    try {
      URI resolved = mapper.resolve(path);
      LOGGER.trace(String.format("import-map.resolve(%s) : %s", path, resolved));
      switch (resolved.getScheme()) {
        case "file":
          file = vertx.resolveFile(resolved.getPath());
          break;
        case "http":
        case "https":
          try {
            return parsePath(resolved.toURL());
          } catch (MalformedURLException e) {
            throw new InvalidPathException(path, e.getMessage());
          }
        default:
          throw new IllegalArgumentException("unsupported scheme: " + resolved.getScheme());
      }
    } catch (UnmappedBareSpecifierException e) {
      LOGGER.debug("Failed to resolve module", e);
      // bare specifier
      file = new File(baseDir, path);
    } catch (URISyntaxException e) {
      throw new InvalidPathException(path, e.getMessage());
    }

    // if it's a download, get the file to the download dir
    // if not, continue the processing...
    if (!fetchIfNeeded(file, path)) {
      // force resolve through vertx, this allows fixing paths from cache back to the right location
      file = vertx.resolveFile(file.getPath());
    }

    return file.toPath();
  }

  private boolean fetchIfNeeded(File file, String path) {
    LOGGER.trace(String.format("fetchIfNeeded(%s, %s)", file, path));

    if (path.startsWith(downloadDir)) {
      // download if missing
      if (!file.exists()) {
        // build an URL from the path
        String target = relativize(downloadDir, path);
        int split = target.indexOf(File.separator);
        String source = target.substring(0, split);
        // can we map the hash to an url?
        if (!urlMap.containsKey(source)) {
          throw new InvalidPathException(path, "Cannot resolve the source of the hash: " + source);
        }
        try {
          // try to download
          downloadTo(new URL(urlMap.get(source) + target.substring(split)), file);
        } catch (IOException e) {
          throw new InvalidPathException(path, e.getMessage());
        }
      }
      return true;
    }
    return false;
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
    File file = vertx.resolveFile(relativize(cwd, name));

    boolean isDownloaded = fetchIfNeeded(file, name);

    // if file doesn't exist, we try to guess it it's missing the extension or is an index
    // if it still fails, it falls back to the original file
    if (!isDownloaded && !file.exists()) {
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

  @Override
  public boolean isSameFile(Path path1, Path path2, LinkOption... options) throws IOException {
    // TODO: also consider cache files vs non cached ones
    if (toAbsolutePath(path1).equals(toAbsolutePath(path2))) {
      return true;
    }
    return toRealPath(path1, options).equals(toRealPath(path2, options));
  }

  @Override
  public Path getTempDirectory() {
    return new File(System.getProperty("java.io.tmpdir")).toPath();
  }
}
