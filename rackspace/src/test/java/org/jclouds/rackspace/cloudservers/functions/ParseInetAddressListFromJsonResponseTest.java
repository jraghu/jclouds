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
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.ImmutableList;

/**
 * Tests behavior of {@code ParseInetAddressListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseInetAddressListFromJsonResponseTest")
public class ParseInetAddressListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testPublic() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream(
               "/cloudservers/test_list_addresses_public.json");

      ParseInetAddressListFromJsonResponse parser = new ParseInetAddressListFromJsonResponse(i
               .getInstance(Gson.class));
      List<String> response = parser.apply(is);
      assertEquals(response, ImmutableList.of("67.23.10.132", "67.23.10.131"));
   }

   public void testPrivate() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream(
               "/cloudservers/test_list_addresses_private.json");

      ParseInetAddressListFromJsonResponse parser = new ParseInetAddressListFromJsonResponse(i
               .getInstance(Gson.class));
      List<String> response = parser.apply(is);
      assertEquals(response, ImmutableList.of("10.176.42.16"));
   }
}
