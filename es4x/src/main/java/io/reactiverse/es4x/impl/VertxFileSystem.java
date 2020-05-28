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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.Json;
import org.graalvm.polyglot.io.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

public final class VertxFileSystem implements FileSystem {

  private static final List<String> EXTENSIONS = Arrays.asList(".mjs", ".js");
  private static final Path EMPTY = Paths.get("");

  private final String prefix = System.getProperty("baseUrl", "");
  private final VertxInternal vertx;

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


  public VertxFileSystem(final Vertx vertx) {
    Objects.requireNonNull(vertx, "vertx must be non null.");
    this.vertx = (VertxInternal) vertx;
  }

  private File resolve(String path) throws IOException {
    final File fallback = vertx.resolveFile(path);
    File resolved;

    if (path.startsWith(".") || path.startsWith("/")) {
      resolved = resolveFile(fallback);
      // try as dir
      if (resolved == null) {
        resolved = resolveDir(fallback);
      }
    } else {
      // module
      resolved = resolveFile(vertx.resolveFile("node_modules/" + path));

      if (resolved == null) {
        resolved = resolveDir(vertx.resolveFile("node_modules/" + path));
      }
    }

    // fallback, this is used to resolve files that are not
    // necessarily a module but requested by graalvm
    if (resolved == null && fallback.exists()) {
      return fallback.getCanonicalFile();
    }

    return resolved;
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

    return null;
  }

  private File resolveDir(File dir) throws IOException {
    File pkgfile = new File(dir, "package.json");
    if (pkgfile.isFile()) {
      try {
        Map pkg = Json.decodeValue(Buffer.buffer(Files.readAllBytes(pkgfile.toPath())), Map.class);
        if (pkg.containsKey("module") && pkg.get("module") instanceof String) {
          String module = (String) pkg.get("module");

          if (".".equals(module) || "./".equals(module)) {
            module = "index";
          }

          // attempt to load
          File resolved = resolveFile(new File(dir, module));
          if (resolved == null) {
            resolved = resolveDir(new File(dir, module));
          }

          if (resolved != null) {
            return resolved.getCanonicalFile();
          }
        }
      } catch (IOException e) {
        // can't parse, assume invalid
        return null;
      }
    }

    return resolveFile(new File(dir, "index"));
  }

  @Override
  public Path parsePath(URI uri) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path parsePath(String path) {
    // EMPTY shortcut
    if ("".equals(path)) {
      return EMPTY;
    }

    // apply the prefix is the path is relative
    if (path.charAt(0) == '.' && prefix.length() > 0) {
      path = prefix + path;
    }

    try {
      File resolved = resolve(path);
      // can't resolve
      if (resolved == null) {
        throw new InvalidPathException(path, "Does not resolve to a File");
      }
      return resolved.toPath();
    } catch (IOException e) {
      throw new InvalidPathException(path, e.getMessage());
    }
  }

  @Override
  public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
    if (isFollowLinks(linkOptions)) {
      path
        .getFileSystem()
        .provider()
        .checkAccess(resolveRelative(path), modes.toArray(new AccessMode[0]));
    } else if (modes.isEmpty()) {
      Files.readAttributes(path, "isRegularFile", LinkOption.NOFOLLOW_LINKS);
    } else {
      throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
    }
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    Files.createDirectory(resolveRelative(dir), attrs);
  }

  @Override
  public void delete(Path path) throws IOException {
    Files.delete(resolveRelative(path));
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    Files.copy(resolveRelative(source), resolveRelative(target), options);
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    Files.move(resolveRelative(source), resolveRelative(target), options);
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    return Files.newByteChannel(resolveRelative(path), options, attrs);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    return Files.newDirectoryStream(resolveRelative(dir), filter);
  }

  @Override
  public void createLink(Path link, Path existing) throws IOException {
    Files.createLink(resolveRelative(link), resolveRelative(existing));
  }

  @Override
  public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
    Files.createSymbolicLink(resolveRelative(link), resolveRelative(target), attrs);
  }

  @Override
  public Path readSymbolicLink(Path link) throws IOException {
    return Files.readSymbolicLink(resolveRelative(link));
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    return Files.readAttributes(resolveRelative(path), attributes, options);
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
    Files.setAttribute(resolveRelative(path), attribute, value, options);
  }

  @Override
  public Path toAbsolutePath(Path path) {
    // special case
    if ("".equals(path.toString())) {
      return EMPTY;
    }
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    final Path resolved = vertx.resolveFile(path.toString()).toPath();

    if (resolved.isAbsolute()) {
      return path;
    }

    return resolved.toAbsolutePath();
  }

  @Override
  public void setCurrentWorkingDirectory(Path currentWorkingDirectory) {
    throw new IllegalStateException("Changing Vert.x Current Working Directory is not allowed after startup.");
  }

  @Override
  public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
    final Path resolvedPath = resolveRelative(path);
    return resolvedPath.toRealPath(linkOptions);
  }

  private Path resolveRelative(Path path) throws IOException {
    if (path.isAbsolute()) {
      return path;
    }
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    File resolved = resolve(path.toString());
    if (resolved == null) {
      throw new FileNotFoundException(path.toString());
    }

    return resolved.toPath();
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
