package io.reactiverse.es4x.sourcemap;

import java.util.Arrays;

/**
 * We encode our variable length numbers as base64 encoded strings with
 * the least significant digit coming first.  Each base64 digit encodes
 * a 5-bit value (0-31) and a continuation bit.  Signed values can be
 * represented by using the least significant bit of the value as the
 * sign bit.
 *
 * Code based on Google Closure Compiler https://code.google.com/p/closure-compiler
 */
class Base64VLQ {
  // Utility class.
  private Base64VLQ() {}

  // A map used to convert integer values in the range 0-63 to their base64 values.
  private static final String BASE64_MAP =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
      "abcdefghijklmnopqrstuvwxyz" +
      "0123456789+/";

  // A map used to convert base64 character into integer values.
  private static final int[] BASE64_DECODE_MAP = new int[256];

  static {
    Arrays.fill(BASE64_DECODE_MAP, -1);
    for (int i = 0; i < BASE64_MAP.length(); i++)
      BASE64_DECODE_MAP[BASE64_MAP.charAt(i)] = i;
  }
  // A Base64 VLQ digit can represent 5 bits, so it is base-32.
  private static final int VLQ_BASE_SHIFT = 5;
  private static final int VLQ_BASE = 1 << VLQ_BASE_SHIFT;

  // A mask of bits for a VLQ digit (11111), 31 decimal.
  private static final int VLQ_BASE_MASK = VLQ_BASE-1;

  // The continuation bit is the 6th bit.
  private static final int VLQ_CONTINUATION_BIT = VLQ_BASE;

  /**
   * Converts to a two-complement value from a value where the sign bit is
   * is placed in the least significant bit.  For example, as decimals:
   *   2 (10 binary) becomes 1, 3 (11 binary) becomes -1
   *   4 (100 binary) becomes 2, 5 (101 binary) becomes -2
   */
  private static int fromVLQSigned(int value) {
    boolean negate = (value & 1) == 1;
    value = value >> 1;
    return negate ? -value : value;
  }

  /**
   * A simple interface for advancing through a sequence of characters, that
   * communicates that advance back to the source.
   */
  interface CharIterator {
    boolean hasNext();
    char next();
  }

  /**
   * Decodes the next VLQValue from the provided CharIterator.
   */
  public static int decode(CharIterator in) {
    int result = 0;
    boolean continuation;
    int shift = 0;
    do {
      char c = in.next();
      int digit = fromBase64(c);
      continuation = (digit & VLQ_CONTINUATION_BIT) != 0;
      digit &= VLQ_BASE_MASK;
      result = result + (digit << shift);
      shift = shift + VLQ_BASE_SHIFT;
    } while (continuation);

    return fromVLQSigned(result);
  }

  /**
   * @param c A base64 digit.
   * @return A value in the range of 0-63.
   */
  public static int fromBase64(char c) {
    int result = BASE64_DECODE_MAP[c];
    assert (result != -1) : "invalid char";
    return BASE64_DECODE_MAP[c];
  }
}
