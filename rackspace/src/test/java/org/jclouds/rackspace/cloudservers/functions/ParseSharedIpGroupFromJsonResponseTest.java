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

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSharedIpGroupFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.ParseSharedIpGroupFromJsonResponseTest")
public class ParseSharedIpGroupFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_get_sharedipgroup_details.json");

      ParseSharedIpGroupFromJsonResponse parser = new ParseSharedIpGroupFromJsonResponse(i
               .getInstance(Gson.class));
      SharedIpGroup response = parser.apply(is);
      assertEquals(response.getId(), 1234);
      assertEquals(response.getName(), "Shared IP Group 1");
      assertEquals(response.getServers(), ImmutableList.of(422));
   }
}