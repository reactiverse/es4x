package io.reactiverse.es4x.runtime.commands;

final class Artifact {
  
  private String groupId;
  private String artifactId;
  private String version;
  private String scope;
  private String classifier;

  public String getGroupId() {
    return groupId;
  }

  public Artifact setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public Artifact setArtifactId(String artifactId) {
    this.artifactId = artifactId;
    return this;
  }

  public String getVersion() {
    return version != null ? version : "LATEST";
  }

  public Artifact setVersion(String version) {
    this.version = version;
    return this;
  }

  public String getScope() {
    return scope != null ? scope : "compile";
  }

  public Artifact setScope(String scope) {
    this.scope = scope;
    return this;
  }

  public String getClassifier() {
    return classifier;
  }

  public Artifact setClassifier(String classifier) {
    this.classifier = classifier;
    return this;
  }
}
