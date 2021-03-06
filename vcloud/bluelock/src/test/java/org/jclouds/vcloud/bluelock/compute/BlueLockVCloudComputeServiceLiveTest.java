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

package org.jclouds.vcloud.bluelock.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.http.HttpResponseException;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeServiceLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.BlueLockVCloudComputeServiceLiveTest")
public class BlueLockVCloudComputeServiceLiveTest extends VCloudComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "bluelock";
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_64);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), "133");
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
   }

   @Override
   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      Template template = super.buildTemplate(templateBuilder);
      Image image = template.getImage();
      assert image.getDefaultCredentials().account != null : image;
      assert image.getDefaultCredentials().key != null : image;
      return template;
   }

   @Test
   public void testErrorWhereAllNetworksReturn403() throws Exception {
      VCloudClient bluelockClient = VCloudClient.class.cast(context.getProviderSpecificContext()
               .getApi());
      for (String i : new String[] { "1", "2", "3", "4" }) {
         try {
            bluelockClient.getNetwork(i);
         } catch (HttpResponseException e) {
            assertEquals(e.getResponse().getStatusCode(), 403);
         }
      }

   }
}
