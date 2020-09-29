/*
 * Copyright 2019 Red Hat, Inc.
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
package io.reactiverse.es4x.commands;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.*;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.reactiverse.es4x.cli.Helper.warn;

public final class Resolver {

  private static final String USER_HOME = System.getProperty("user.home");
  private static final String FILE_SEP = System.getProperty("file.separator");
  private static final String DEFAULT_MAVEN_LOCAL = USER_HOME + FILE_SEP + ".m2" + FILE_SEP + "repository";
  private static final String DEFAULT_MAVEN_REMOTE = "https://repo1.maven.org/maven2/";

  private final RepositorySystem system;
  private final LocalRepository localRepo;
  private final List<RemoteRepository> remotes = new ArrayList<>();

  public Resolver() throws MalformedURLException {
    DefaultServiceLocator locator = getDefaultServiceLocator();

    system = locator.getService(RepositorySystem.class);
    localRepo = new LocalRepository(DEFAULT_MAVEN_LOCAL);

    if (isOffline()) {
      warn("Maven online artifact resolution disabled.");
      // we stop here as the users want to be offline
      return;
    }

    // add user repo
    String registry = System.getProperty("maven.registry", System.getenv("MAVEN_REGISTRY"));
    if (registry != null && !"".equals(registry)) {
      URL url = new URL(registry);
      Authentication auth = extractAuth(url);
      if (auth != null) {
        url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
      }
      RemoteRepository.Builder builder = new RemoteRepository.Builder("registry", "default", url.toString());
      if (auth != null) {
        builder.setAuthentication(auth);
      }
      remotes.add(builder.build());
    }

    remotes.add(
      new RemoteRepository
        .Builder("central", "default", DEFAULT_MAVEN_REMOTE)
        .setSnapshotPolicy(new RepositoryPolicy(false, RepositoryPolicy.UPDATE_POLICY_NEVER, RepositoryPolicy.CHECKSUM_POLICY_FAIL)).build());
  }

  private static DefaultServiceLocator getDefaultServiceLocator() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    return locator;
  }

  private static boolean isOffline() {
    try {
      return Boolean.parseBoolean(System.getenv("npm_config_offline"));
    } catch (IllegalArgumentException | NullPointerException e) {
      return false;
    }
  }

  /**
   * Resolve the given artifact.
   *
   * @param artifacts the artifact
   * @return the list of artifact
   */
  public List<Artifact> resolve(String root, Collection<String> artifacts) {

    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

    session.setTransferListener(new ConsoleTransferListener());
    session.setRepositoryListener(new ConsoleRepositoryListener());

    DependencyFilter filter =
      DependencyFilterUtils.andFilter(
        DependencyFilterUtils.classpathFilter(
          JavaScopes.COMPILE
        ),
        // Remove optionals and dependencies of optionals
        (dependencyNode, list) -> {
          for (DependencyNode parent : list) {
            if (parent.getDependency().isOptional()) {
              return false;
            }
          }

          return !dependencyNode.getDependency().isOptional();
        },

        // Remove excluded dependencies
        (dependencyNode, list) -> {
          // Build the list of exclusion, traverse the tree.
          Collection<Exclusion> ex = new ArrayList<>();
          for (DependencyNode parent : list) {
            ex.addAll(parent.getDependency().getExclusions());
          }

          for (Exclusion e : ex) {
            // Check the the passed artifact is excluded
            if (e.getArtifactId().equals(dependencyNode.getArtifact().getArtifactId())
              && e.getGroupId().equals(dependencyNode.getArtifact().getGroupId())) {
              return false;
            }

            // Check if a parent artifact is excluded
            for (DependencyNode parent : list) {
              if (e.getArtifactId().equals(parent.getArtifact().getArtifactId())
                && e.getGroupId().equals(parent.getArtifact().getGroupId())) {
                return false;
              }
            }
          }
          return true;
        },

        // Remove provided dependencies and transitive dependencies of provided dependencies
        (dependencyNode, list) -> {
          for (DependencyNode parent : list) {
            if (!parent.getDependency().getScope().toLowerCase().equals("compile")) {
              return false;
            }
          }
          return dependencyNode.getDependency().getScope().toLowerCase().equals("compile");
        }
      );


    List<ArtifactResult> artifactResults;
    try {
      CollectRequest collectRequest = new CollectRequest();

      collectRequest.setRoot(new Dependency(new DefaultArtifact(root), JavaScopes.COMPILE));

      for (String artifact : artifacts) {
        collectRequest.addDependency(new Dependency(new DefaultArtifact(artifact), JavaScopes.COMPILE));
      }

      collectRequest.setRepositories(remotes);
      DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, filter);
      artifactResults =
        system.resolveDependencies(session, dependencyRequest).getArtifactResults();

    } catch (DependencyResolutionException e) {
      throw new IllegalArgumentException("Cannot resolve artifacts " + artifacts.toString() +
        " in maven repositories: " + e.getMessage());
    }

    return artifactResults.stream().map(ArtifactResult::getArtifact)
      .collect(Collectors.toList());
  }

  private static Authentication extractAuth(URL url) {
    String userInfo = url.getUserInfo();
    if (userInfo != null) {
      AuthenticationBuilder authBuilder = new AuthenticationBuilder();
      int sep = userInfo.indexOf(':');
      String defaultCharset = Charset.defaultCharset().toString();
      try {
        if (sep != -1) {
          authBuilder.addUsername(URLDecoder.decode(userInfo.substring(0, sep), defaultCharset));
          authBuilder.addPassword(URLDecoder.decode(userInfo.substring(sep + 1), defaultCharset));
        } else {
          authBuilder.addUsername(URLDecoder.decode(userInfo, defaultCharset));
        }
      } catch (final UnsupportedEncodingException e) {
        throw new IllegalArgumentException(
          "maven registry url is not encoded with " + defaultCharset +
          " charset and percent-encoded username/password: " + url,
          e);
      }
      return authBuilder.build();
    }
    return null;
  }
}
