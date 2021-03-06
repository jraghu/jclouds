/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.config.GoGridContextModule;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
@Test(groups = "unit", testName = "gogrid.ParseServerNameToCredentialsMapFromJsonResponseTest")
public class ParseServerNameToCredentialsMapFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_credentials_list.json");

      ParseServerNameToCredentialsMapFromJsonResponse parser = new ParseServerNameToCredentialsMapFromJsonResponse(
               i.getInstance(Gson.class));
      Map<String, Credentials> response = parser.apply(is);
      assertEquals(response.size(), 6);
   }

   Injector i = Guice.createInjector(new ParserModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(GoGridContextModule.DateSecondsAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      @SuppressWarnings( { "unused", "unchecked" })
      @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
      public Map<Class, Object> provideCustomAdapterBindings() {
         Map<Class, Object> bindings = Maps.newHashMap();
         bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
         bindings.put(ServerImageType.class, new CustomDeserializers.ServerImageTypeAdapter());
         bindings.put(ServerImageState.class, new CustomDeserializers.ServerImageStateAdapter());
         bindings.put(ServerImageState.class, new CustomDeserializers.ServerImageStateAdapter());
         return bindings;
      }
   });

}
