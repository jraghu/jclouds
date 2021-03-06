/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rackspace;

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class StubRackspaceAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Authentication
   protected URI provideAuthenticationURI() {
      return URI.create("http://localhost/rackspacestub/authentication");
   }

   @Provides
   @Authentication
   protected String provideAuthenticationToken(@Named(PROPERTY_RACKSPACE_USER) String user,
            @Named(PROPERTY_RACKSPACE_KEY) String key) {
      return user + ":" + key;
   }

   @Provides
   @Singleton
   @CloudFiles
   protected URI provideStorageUrl() {
      return URI.create("http://localhost/rackspacestub/cloudfiles");
   }

   @Provides
   @Singleton
   @CloudServers
   protected URI provideServerUrl() {
      return URI.create("http://localhost/rackspacestub/cloudservers");
   }

   @Provides
   @Singleton
   @CloudFilesCDN
   protected URI provideCDNUrl() {
      return URI.create("http://localhost/rackspacestub/cloudfilescdn");
   }
}