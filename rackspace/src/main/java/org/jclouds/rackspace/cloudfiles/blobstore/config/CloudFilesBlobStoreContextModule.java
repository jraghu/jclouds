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
package org.jclouds.rackspace.cloudfiles.blobstore.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesAsyncBlobStore;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule;
import org.jclouds.rackspace.config.RackspaceLocationsModule;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link CloudFilesBlobStoreContext}; requires {@link CloudFilesAsyncBlobStore}
 * bound.
 * 
 * @author Adrian Cole
 */
public class CloudFilesBlobStoreContextModule extends CloudFilesContextModule {
   private final String providerName;

   public CloudFilesBlobStoreContextModule(String providerName) {
      this.providerName = providerName;
   }

   @Override
   protected void configure() {
      super.configure();
      install(new RackspaceLocationsModule(providerName));
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
      bind(AsyncBlobStore.class).to(CloudFilesAsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(CloudFilesBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStoreContext.class).to(
               new TypeLiteral<BlobStoreContextImpl<CloudFilesClient, CloudFilesAsyncClient>>() {
               }).in(Scopes.SINGLETON);
   }

}
