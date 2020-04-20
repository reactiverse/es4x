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
import org.graalvm.polyglot.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public final class VertxFileSystem implements FileSystem {

  private static final FileSystemProvider delegate = FileSystems.getDefault().provider();

  private final VertxInternal vertx;
  private final String cwd;
  private final Path tmpDir;

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

    int len = cwd.length();

    // ensure it doesn't end with /
    if (cwd.charAt(len - 1) == '/') {
      cwd = cwd.substring(0, len - 1);
    }

    return cwd;
  }

  public VertxFileSystem(final Vertx vertx) {
    Objects.requireNonNull(vertx, "vertx must be non null.");
    this.vertx = (VertxInternal) vertx;
    this.cwd = getCWD() + "/";
    tmpDir = parsePath(System.getProperty("java.io.tmpdir"));
  }

  @Override
  public Path parsePath(URI uri) {
    try {
      return Paths.get(uri);
    } catch (IllegalArgumentException | FileSystemNotFoundException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @Override
  public Path parsePath(String path) {
    return Paths.get(path);
  }

  @Override
  public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
    if (isFollowLinks(linkOptions)) {
      delegate.checkAccess(resolveRelative(path), modes.toArray(new AccessMode[0]));
    } else if (modes.isEmpty()) {
      delegate.readAttributes(resolveRelative(path), "isRegularFile", LinkOption.NOFOLLOW_LINKS);
    } else {
      throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
    }
  }

  @Override
  public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
    delegate.createDirectory(resolveRelative(dir), attrs);
  }

  @Override
  public void delete(Path path) throws IOException {
    delegate.delete(resolveRelative(path));
  }

  @Override
  public void copy(Path source, Path target, CopyOption... options) throws IOException {
    delegate.copy(resolveRelative(source), resolveRelative(target), options);
  }

  @Override
  public void move(Path source, Path target, CopyOption... options) throws IOException {
    delegate.move(resolveRelative(source), resolveRelative(target), options);
  }

  @Override
  public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
    return delegate.newByteChannel(resolveRelative(path), options, attrs);
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
    return delegate.newDirectoryStream(resolveRelative(dir), filter);
  }

  @Override
  public void createLink(Path link, Path existing) throws IOException {
    delegate.createLink(resolveRelative(link), resolveRelative(existing));
  }

  @Override
  public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
    delegate.createSymbolicLink(resolveRelative(link), resolveRelative(target), attrs);
  }

  @Override
  public Path readSymbolicLink(Path link) throws IOException {
    return delegate.readSymbolicLink(resolveRelative(link));
  }

  @Override
  public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
    return delegate.readAttributes(resolveRelative(path), attributes, options);
  }

  @Override
  public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
    delegate.setAttribute(resolveRelative(path), attribute, value, options);
  }

  @Override
  public Path toAbsolutePath(Path path) {
    return resolveRelative(path)
      .toAbsolutePath();
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

  @Override
  public Path getTempDirectory() {
    return tmpDir;
  }

  private Path resolveRelative(Path path) {
    String fileName = path.toString();
    if (fileName.startsWith(cwd)) {
      fileName = fileName.substring(cwd.length());
    }

    return vertx.resolveFile(fileName).toPath();
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
