package io.reactiverse.es4x.sourcemap;

/**
 * Mapping for Source Map.
 */
public class Mapping {
  private final int generatedLine;
  private final int generatedColumn;
  private final int sourceLine;
  private final int sourceColumn;
  private final String sourceFileName;
  private final String sourceSymbolName;

  public Mapping(int generatedLine, int generatedColumn, int sourceLine, int sourceColumn, String sourceFileName, String sourceSymbolName) {
    this.generatedLine = generatedLine;
    this.generatedColumn = generatedColumn;
    this.sourceLine = sourceLine;
    this.sourceColumn = sourceColumn;
    this.sourceFileName = sourceFileName;
    this.sourceSymbolName = sourceSymbolName;
  }

  public String toString() {
    return "Mapping " + generatedLine + ":" + generatedColumn + " -> " + sourceFileName + ":" + sourceLine + ":" + sourceColumn;
  }

  public int getGeneratedLine() {
    return generatedLine;
  }

  public int getGeneratedColumn() {
    return generatedColumn;
  }

  public int getSourceLine() {
    return sourceLine;
  }

  public int getSourceColumn() {
    return sourceColumn;
  }

  public String getSourceFileName() {
    return sourceFileName;
  }

  public String getSourceSymbolName() {
    return sourceSymbolName;
  }
}
