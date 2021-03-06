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
package org.jclouds.azure.storage.blob.config;

import java.net.URI;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.internal.StubAzureBlobAsyncClient;
import org.jclouds.blobstore.config.TransientBlobStoreModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * adds a stub alternative to invoking AzureBlob
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AzureBlobStubClientModule extends
         RestClientModule<AzureBlobClient, AzureBlobAsyncClient> {

   public AzureBlobStubClientModule() {
      super(AzureBlobClient.class, AzureBlobAsyncClient.class);
   }

   protected void configure() {
      super.configure();
      install(new AzureBlobModule());
      install(new ParserModule());
      install(new TransientBlobStoreModule());
      bind(URI.class).annotatedWith(AzureBlob.class).toInstance(
               URI.create("https://localhost/azurestub"));
   }

   @Override
   protected void bindAsyncClient() {
      bind(AzureBlobAsyncClient.class).to(StubAzureBlobAsyncClient.class).asEagerSingleton();
   }
}