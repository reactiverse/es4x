package io.reactiverse.es4x;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.*;

public class BufferUtils {

  private static final Base64.Encoder B64ENC = Base64.getUrlEncoder();
  private static final Base64.Decoder B64DEC = Base64.getUrlDecoder();

  public static ByteBuffer alloc(int length) {
    return ByteBuffer.allocateDirect(length);
  }

  public static int write(ByteBuffer buffer, String encoding, String src, int offset, int length) {

    boolean loweredCase = false;
    for (; ; ) {
      switch (encoding) {
//        case "hex":
//          return hexWrite(this, string, offset, length);
//
        case "utf8":
        case "utf-8":
          return blit(src.getBytes(UTF_8), buffer, offset, length);
        case "ascii":
          return blit(src.getBytes(US_ASCII), buffer, offset, length);
        case "latin1":
        case "binary":
          return blit(src.getBytes(ISO_8859_1), buffer, offset, length);
        case "base64":
          return blit(B64DEC.decode(src), buffer, offset, length);
        case "ucs2":
        case "ucs-2":
        case "utf16le":
        case "utf-16le":
          return blit(src.getBytes(UTF_16LE), buffer, offset, length);

        default:
          if (loweredCase) {
            throw new RuntimeException("Unknown encoding: " + encoding);
          }
          encoding = encoding.toLowerCase();
          loweredCase = true;
      }
    }
  }

  private static int blit(byte[] src, ByteBuffer dst, int offset, int length) {
    int i;

    for (i = 0; i < length; ++i) {
      if ((i + offset >= dst.capacity()) || (i >= src.length)) break;
      dst.put(i + offset, src[i]);
    }
    return i;
  }

  public static String utf8Slice(ByteBuffer buf, int start, int end) {
    byte[] slice = new byte[end - start];
    buf.get(slice, start, end);
    return new String(slice, UTF_8);
  }
}
