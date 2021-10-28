package io.reactiverse.es4x.sourcemap;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Parsing SourceMap version 3.
 * <p>
 * Code based on Google Closure Compiler https://code.google.com/p/closure-compiler
 */
public class SourceMap {
  static final int UNMAPPED = -1;
  private JsonArray sourceFileNames;
  private JsonArray sourceSymbolNames;
  private ArrayList<ArrayList<Mapping>> lines = null;
  private String sourceRoot;

  public SourceMap(String sourceMapData) {
    parse(sourceMapData);
  }

  /**
   * Parses the given contents containing a source map.
   */
  private void parse(String sourceMapData) {
    JsonObject sourceMapRoot = new JsonObject(sourceMapData);

    // Check basic assertions about the format.
    int version = sourceMapRoot.getInteger("version");
    if (version != 3) throw new RuntimeException("Unknown version: " + version);

    sourceFileNames = sourceMapRoot.getJsonArray("sources");
    sourceSymbolNames = sourceMapRoot.getJsonArray("names");

    lines = new ArrayList<>();

    new MappingBuilder(sourceMapRoot.getString("mappings")).build();
  }

  public Mapping getMapping(int lineNumber, int column) {
    if (lineNumber < 0 || lineNumber >= lines.size()) return null;

    if (lineNumber < 0) throw new RuntimeException("invalid line number!");
    if (column < 0) throw new RuntimeException("invalid column number!");


    // If the line is empty return the previous mapping.
    if (lines.get(lineNumber) == null) return getPreviousMapping(lineNumber);

    ArrayList<Mapping> entries = lines.get(lineNumber);
    // No empty lists.
    if (entries.isEmpty()) throw new RuntimeException("empty list of entries!");
    if (entries.get(0).getGeneratedColumn() > column) return getPreviousMapping(lineNumber);

    int index = search(entries, column, 0, entries.size() - 1);
    if (index < 0) throw new RuntimeException("can't find entry!");
    return getMappingForEntry(entries.get(index));
  }

  public void eachMapping(java.util.function.Consumer<Mapping> cb) {
    for (List<Mapping> line : lines) {
      if (line != null) {
        for (Mapping mapping : line) cb.accept(mapping);
      }
    }
  }

  public Collection<String> getSourceFileNames() {
    return sourceFileNames.getList();
  }

  public Collection<String> getSourceSymbolNames() {
    return sourceSymbolNames.getList();
  }

  public String getSourceRoot() {
    return this.sourceRoot;
  }

  private class MappingBuilder {
    private static final int MAX_ENTRY_VALUES = 5;
    private final StringCharIterator content;
    private int line = 0;
    private int previousCol = 0;
    private int previousSrcId = 0;
    private int previousSrcLine = 0;
    private int previousSrcColumn = 0;
    private int previousNameId = 0;

    MappingBuilder(String lineMap) {
      this.content = new StringCharIterator(lineMap);
    }

    void build() {
      int[] temp = new int[MAX_ENTRY_VALUES];
      ArrayList<Mapping> entries = new ArrayList<Mapping>();
      while (content.hasNext()) {
        // ';' denotes a new line.
        if (tryConsumeToken(';')) {
          // The line is complete, store the result
          completeLine(entries);
          // A new array list for the next line.
          if (!entries.isEmpty()) entries = new ArrayList<Mapping>();
        } else {
          // grab the next entry for the current line.
          int entryValues = 0;
          while (!entryComplete()) {
            temp[entryValues] = nextValue();
            entryValues++;
          }
          Mapping entry = decodeEntry(line, temp, entryValues);

          entries.add(entry);

          // Consume the separating token, if there is one.
          tryConsumeToken(',');
        }
      }

      // Some source map generator (e.g.UglifyJS) generates lines without
      // a trailing line separator. So add the rest of the content.
      if (!entries.isEmpty()) completeLine(entries);
    }

    private void completeLine(ArrayList<Mapping> entries) {
      // The line is complete, store the result for the line,
      // null if the line is empty.
      if (!entries.isEmpty()) lines.add(entries);
      else lines.add(null);
      line++;
      previousCol = 0;
    }

    /**
     * Decodes the next entry, using the previous encountered values to
     * decode the relative values.
     *
     * @param vals        An array of integers that represent values in the entry.
     * @param entryValues The number of entries in the array.
     * @return The entry object.
     */
    private Mapping decodeEntry(int generatedLine, int[] vals, int entryValues) {
      Mapping entry;
      int sourceFileNameIndex;
      String sourceFileName;
      int sourceSymbolNameIndex;
      String sourceSymbolName;
      switch (entryValues) {
        // The first values, if present are in the following order:
        //   0: the starting column in the current line of the generated file
        //   1: the id of the original source file
        //   2: the starting line in the original source
        //   3: the starting column in the original source
        //   4: the id of the original symbol name
        // The values are relative to the last encountered value for that field.
        // Note: the previously column value for the generated file is reset
        // to '0' when a new line is encountered.  This is done in the 'build'
        // method.
        case 1:
          // An unmapped section of the generated file.
          entry = new Mapping(
            generatedLine,
            vals[0] + previousCol,
            UNMAPPED,
            UNMAPPED,
            null,
            null
          );
          // Set the values see for the next entry.
          previousCol = entry.getGeneratedColumn();
          return entry;

        case 4:
          // A mapped section of the generated file.
          sourceFileNameIndex = vals[1] + previousSrcId;
          sourceFileName = sourceFileNames.getString(sourceFileNameIndex);

          entry = new Mapping(
            generatedLine,
            vals[0] + previousCol,
            vals[2] + previousSrcLine,
            vals[3] + previousSrcColumn,
            sourceFileName,
            null);
          // Set the values see for the next entry.
          previousCol = entry.getGeneratedColumn();
          previousSrcLine = entry.getSourceLine();
          previousSrcColumn = entry.getSourceColumn();
          previousSrcId = sourceFileNameIndex;
          return entry;

        case 5:
          // A mapped section of the generated file, that has an associated
          // name.
          sourceFileNameIndex = vals[1] + previousSrcId;
          sourceFileName = sourceFileNames.getString(sourceFileNameIndex);
          sourceSymbolNameIndex = vals[4] + previousNameId;
          sourceSymbolName = sourceSymbolNames.getString(sourceFileNameIndex);

          entry = new Mapping(
            generatedLine,
            vals[0] + previousCol,
            vals[2] + previousSrcLine,
            vals[3] + previousSrcColumn,
            sourceFileName,
            sourceSymbolName
          );
          // Set the values see for the next entry.
          previousCol = entry.getGeneratedColumn();
          previousSrcLine = entry.getSourceLine();
          previousSrcColumn = entry.getSourceColumn();
          previousSrcId = sourceFileNameIndex;
          previousNameId = sourceSymbolNameIndex;
          return entry;

        default:
          throw new IllegalStateException("Unexpected number of values for entry:" + entryValues);
      }
    }

    private boolean tryConsumeToken(char token) {
      if (content.hasNext() && content.peek() == token) {
        // consume the comma
        content.next();
        return true;
      }
      return false;
    }

    private boolean entryComplete() {
      if (!content.hasNext()) return true;
      char c = content.peek();
      return (c == ';' || c == ',');
    }

    private int nextValue() {
      return Base64VLQ.decode(content);
    }
  }

  /**
   * Perform a binary search on the array to find a section that covers
   * the target column.
   */
  private int search(ArrayList<Mapping> entries, int target, int start, int end) {
    while (true) {
      int mid = ((end - start) / 2) + start;
      int compare = compareEntry(entries, mid, target);
      if (compare == 0) return mid;
      else if (compare < 0) {
        // it is in the upper half
        start = mid + 1;
        if (start > end) return end;
      } else {
        // it is in the lower half
        end = mid - 1;
        if (end < start) return end;
      }
    }
  }

  /**
   * Compare an array entry's column value to the target column value.
   */
  private int compareEntry(ArrayList<Mapping> entries, int entry, int target) {
    return entries.get(entry).getGeneratedColumn() - target;
  }

  /**
   * Returns the mapping entry that proceeds the supplied line or null if no
   * such entry exists.
   */
  private Mapping getPreviousMapping(int lineNumber) {
    do {
      if (lineNumber == 0) return null;
      lineNumber--;
    } while (lines.get(lineNumber) == null);
    ArrayList<Mapping> entries = lines.get(lineNumber);
    return getMappingForEntry(entries.get(entries.size() - 1));
  }

  /**
   * Creates an "Mapping" object for the given entry object.
   */
  private Mapping getMappingForEntry(Mapping entry) {
    return (entry.getSourceFileName() == null) ? null : entry;
  }

  /**
   * A implementation of the Base64VLQ CharIterator used for decoding the
   * mappings encoded in the JSON string.
   */
  private static class StringCharIterator implements Base64VLQ.CharIterator {
    final String content;
    final int length;
    int current = 0;

    StringCharIterator(String content) {
      this.content = content;
      this.length = content.length();
    }

    public char next() {
      return content.charAt(current++);
    }

    char peek() {
      return content.charAt(current);
    }

    public boolean hasNext() {
      return current < length;
    }
  }
}
