package io.reactiverse.es4x.impl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class Utils {

  private Utils () {
    throw new UnsupportedOperationException("Cannot instantiate Utils");
  }
  // Create MessageDigest instance for MD5
  static final MessageDigest MD5;

  static {
    try {
      MD5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Cannot instantiate MD5", e);
    }
  }

  public static synchronized String md5(String input) {
    //Add password bytes to digest
    MD5.update(input.getBytes(StandardCharsets.UTF_8));
    //Get the hash's bytes
    byte[] bytes = MD5.digest();
    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
    }
    //Get complete hashed input in hex format
    return sb.toString();
  }

  public static void downloadTo(URL url, File target) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setInstanceFollowRedirects(true);
    conn.setRequestProperty("User-Agent", "es4x/pm");

    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
      throw new IOException(conn.getResponseMessage());
    }

    try (InputStream inputStream = conn.getInputStream()) {
      try (BufferedInputStream reader = new BufferedInputStream(inputStream)) {
        final File parent = target.getParentFile();
        if (!parent.exists()) {
          if (!parent.mkdirs()) {
            throw new RuntimeException("Failed to mkdirs: " + parent);
          }
        }
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

  public static String getManifestAttribute(String attribute, String defaultValue) throws IOException {
    Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
    while (resEnum.hasMoreElements()) {
      URL url = resEnum.nextElement();
      try (InputStream is = url.openStream()) {
        if (is != null) {
          Manifest manifest = new Manifest(is);
          Attributes mainAttribs = manifest.getMainAttributes();
          String mode = mainAttribs.getValue(attribute);
          if(mode != null) {
            return mode;
          }
        }
      } catch (IOException e) {
        // Silently ignore wrong manifests on classpath?
      }
    }

    return defaultValue;
  }
}
