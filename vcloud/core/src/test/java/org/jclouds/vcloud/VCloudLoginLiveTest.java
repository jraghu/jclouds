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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.testng.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code VCloudLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudLoginLiveTest")
public class VCloudLoginLiveTest {

   @RequiresHttp
   @ConfiguresRestClient
   private static final class VCloudLoginRestClientModule extends AbstractModule {

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected VCloudLoginAsyncClient provideVCloudLogin(AsyncClientFactory factory) {
         return factory.create(VCloudLoginAsyncClient.class);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      public BasicAuthentication provideBasicAuthentication(
               @Named(PROPERTY_VCLOUD_USER) String user, @Named(PROPERTY_VCLOUD_KEY) String key,
               EncryptionService encryptionService) throws UnsupportedEncodingException {
         return new BasicAuthentication(user, key, encryptionService);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      @VCloudLogin
      URI provideUri(@Named(PROPERTY_VCLOUD_ENDPOINT) String endpoint) {
         return URI.create(endpoint);
      }

      @Override
      protected void configure() {

      }

   }

   private final class VCloudLoginContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudLoginAsyncClient, VCloudLoginAsyncClient> provideContext(Closer closer,
               VCloudLoginAsyncClient api, @VCloudLogin URI endPoint) {
         return new RestContextImpl<VCloudLoginAsyncClient, VCloudLoginAsyncClient>(closer, api,
                  api, endPoint, "");
      }

      @Override
      protected void configure() {

      }
   }

   private RestContext<VCloudLoginAsyncClient, VCloudLoginAsyncClient> context;

   @Test
   public void testLogin() throws Exception {
      VCloudLoginAsyncClient authentication = context.getAsyncApi();
      for (int i = 0; i < 5; i++) {
         VCloudSession response = authentication.login().get(45, TimeUnit.SECONDS);
         assertNotNull(response);
         assertNotNull(response.getVCloudToken());
         assertNotNull(response.getOrgs());
      }
   }

   @BeforeClass
   void setupFactory() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint")
               + "/v0.8/login";
      String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      context = new RestContextBuilder<VCloudLoginAsyncClient, VCloudLoginAsyncClient>("vcloud",
               VCloudLoginAsyncClient.class, VCloudLoginAsyncClient.class,
               new VCloudPropertiesBuilder(URI.create(endpoint), account, key).build()) {

         public void addContextModule(String providerName, List<Module> modules) {
            modules.add(new VCloudLoginContextModule());
         }

         @Override
         protected void addClientModule(List<Module> modules) {
            modules.add(new VCloudLoginRestClientModule());
         }

      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()))
               .buildContext();
   }
}
