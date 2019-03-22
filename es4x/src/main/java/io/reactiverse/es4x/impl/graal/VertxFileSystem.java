package io.reactiverse.es4x.impl.graal;

import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import org.graalvm.polyglot.io.FileSystem;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class VertxFileSystem implements FileSystem {

  private final VertxInternal vertx;

  public VertxFileSystem(final Vertx vertx) {
    Objects.requireNonNull(vertx, "vertx must be non null.");
    this.vertx = (VertxInternal) vertx;
  }

  @Override
  public Path parsePath(URI uri) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Path parsePath(String path) {
    // TODO: follow http://2ality.com/2018/12/nodejs-esm-phases.html
    return vertx.resolveFile(path).toPath();
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

  private Path resolveRelative(Path path) {
    if (path.isAbsolute()) {
      return path;
    }
    // force all resolutions to go over vertx file resolver to allow
    // getting the right path objects even if on the classpath
    return vertx.resolveFile(path.toString()).toPath();
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
