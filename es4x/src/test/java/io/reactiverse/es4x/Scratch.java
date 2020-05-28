package io.reactiverse.es4x;

import io.reactiverse.es4x.impl.VertxFileSystem;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Scratch {

  public static void main(String[] args) throws URISyntaxException {

    Vertx vertx = Vertx.vertx();

    VertxFileSystem fs = new VertxFileSystem(vertx);

    File cache = ((VertxInternal) vertx).resolveFile("");

    // resolve to node modules
    System.out.println(fs.parsePath("index.js"));
    // resolve to cwd
    System.out.println(fs.parsePath("./index.js"));
    // resolve to cwd
    System.out.println(fs.parsePath("../index.js"));
    // resolve to root
    System.out.println(fs.parsePath("/index.js"));
    // rewrite to cwd
    System.out.println(fs.parsePath(cache.getPath() + "/durp"));
    // attempt download
    System.out.println(fs.parsePath(new URI("https://unpkg.io/@vertx/core@3.9.1/options.mjs")));
    // attempt from download cache
    System.out.println(fs.parsePath("./node_modules/.download/eedc890765ef80e2b57c447a50f911cd/@vertx/core@3.9.1/options.mjs"));
    // cwd
    String cwd = VertxFileSystem.getCWD();
    System.out.println(fs.parsePath(cwd));
    System.out.println(fs.parsePath(cwd.substring(0, cwd.length() - 1)));
  }
}
