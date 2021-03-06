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
package org.jclouds.atmosonline.saas.blobstore.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.atmosonline.saas.AtmosStoragePropertiesBuilder;
import org.jclouds.atmosonline.saas.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.atmosonline.saas.config.AtmosStorageStubClientModule;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.AtmosBlobStoreModuleTest")
public class AtmosBlobStoreModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new AtmosStorageStubClientModule(),
               new AtmosBlobStoreContextModule("atmos"), new ExecutorServiceModule(sameThreadExecutor(),
                        sameThreadExecutor()), new AbstractModule() {
                  @Override
                  protected void configure() {
                     Names.bindProperties(binder(), checkNotNull(new AtmosStoragePropertiesBuilder(
                              "user", "key").build(), "properties"));
                  }
               });
   }
   @Test
   void testContextImpl() {

      Injector injector = createInjector();
      BlobStoreContext handler = injector.getInstance(BlobStoreContext.class);
      assertEquals(handler.getClass(), BlobStoreContextImpl.class);
      ContainsValueInListStrategy valueList = injector
               .getInstance(ContainsValueInListStrategy.class);

      assertEquals(valueList.getClass(), FindMD5InUserMetadata.class);
   }

}