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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code OrgListHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.OrgListHandlerTest")
public class OrgListHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/orglist.xml");

      Map<String, NamedResource> result = factory.create(injector.getInstance(OrgListHandler.class))
               .parse(is);
      assertEquals(result, ImmutableMap.of("adrian@jclouds.org", new NamedResourceImpl("48",
               "adrian@jclouds.org", VCloudMediaType.ORG_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"))));
   }
}
