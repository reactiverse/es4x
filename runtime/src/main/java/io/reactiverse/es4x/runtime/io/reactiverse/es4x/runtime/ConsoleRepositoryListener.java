package io.reactiverse.es4x.runtime.io.reactiverse.es4x.runtime;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.PrintStream;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * A simplistic repository listener that logs events to the console.
 */
public class ConsoleRepositoryListener
  extends AbstractRepositoryListener
{

  private PrintStream out;

  public ConsoleRepositoryListener()
  {
    this( null );
  }

  public ConsoleRepositoryListener( PrintStream out )
  {
    this.out = ( out != null ) ? out : System.out;
  }

  public void artifactDeployed( RepositoryEvent event )
  {
    out.println( "Deployed " + event.getArtifact() + " to " + event.getRepository() );
  }

  public void artifactDeploying( RepositoryEvent event )
  {
    out.println( "Deploying " + event.getArtifact() + " to " + event.getRepository() );
  }

  public void artifactDescriptorInvalid( RepositoryEvent event )
  {
    out.println( "Invalid artifact descriptor for " + event.getArtifact() + ": "
      + event.getException().getMessage() );
  }

  public void artifactDescriptorMissing( RepositoryEvent event )
  {
    out.println( "Missing artifact descriptor for " + event.getArtifact() );
  }

  public void artifactInstalled( RepositoryEvent event )
  {
    out.println( "Installed " + event.getArtifact() + " to " + event.getFile() );
  }

  public void artifactInstalling( RepositoryEvent event )
  {
    out.println( "Installing " + event.getArtifact() + " to " + event.getFile() );
  }

  public void artifactResolved( RepositoryEvent event )
  {
    out.println( "Resolved artifact " + event.getArtifact() + " from " + event.getRepository() );
  }

  public void artifactDownloading( RepositoryEvent event )
  {
    out.println( "Downloading artifact " + event.getArtifact() + " from " + event.getRepository() );
  }

  public void artifactDownloaded( RepositoryEvent event )
  {
    out.println( "Downloaded artifact " + event.getArtifact() + " from " + event.getRepository() );
  }

  public void artifactResolving( RepositoryEvent event )
  {
    out.println( "Resolving artifact " + event.getArtifact() );
  }

  public void metadataDeployed( RepositoryEvent event )
  {
    out.println( "Deployed " + event.getMetadata() + " to " + event.getRepository() );
  }

  public void metadataDeploying( RepositoryEvent event )
  {
    out.println( "Deploying " + event.getMetadata() + " to " + event.getRepository() );
  }

  public void metadataInstalled( RepositoryEvent event )
  {
    out.println( "Installed " + event.getMetadata() + " to " + event.getFile() );
  }

  public void metadataInstalling( RepositoryEvent event )
  {
    out.println( "Installing " + event.getMetadata() + " to " + event.getFile() );
  }

  public void metadataInvalid( RepositoryEvent event )
  {
    out.println( "Invalid metadata " + event.getMetadata() );
  }

  public void metadataResolved( RepositoryEvent event )
  {
    out.println( "Resolved metadata " + event.getMetadata() + " from " + event.getRepository() );
  }

  public void metadataResolving( RepositoryEvent event )
  {
    out.println( "Resolving metadata " + event.getMetadata() + " from " + event.getRepository() );
  }

}
