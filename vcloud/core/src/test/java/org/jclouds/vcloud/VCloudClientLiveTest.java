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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudClientLiveTest")
public class VCloudClientLiveTest {

   protected VCloudClient connection;
   protected String account;

   @Test
   public void testOrganization() throws Exception {
      Organization response = connection.getDefaultOrganization();
      assertNotNull(response);
      assertNotNull(response.getId());
      assertNotNull(account);
      assert response.getCatalogs().size() >= 1;
      assert response.getTasksLists().size() >= 1;
      assert response.getVDCs().size() >= 1;
      assertEquals(connection.getOrganization(response.getId()), response);
   }

   @Test
   public void testCatalog() throws Exception {
      Catalog response = connection.getDefaultCatalog();
      assertNotNull(response);
      assertNotNull(response.getId());
      assertNotNull(response.getName());
      assertNotNull(response.getLocation());
      assertEquals(connection.getCatalog(response.getId()), response);
   }

   @Test
   public void testGetNetwork() throws Exception {
      VDC response = connection.getDefaultVDC();
      for (NamedResource resource : response.getAvailableNetworks().values()) {
         if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
            Network item = connection.getNetwork(resource.getId());
            assertNotNull(item);
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      Catalog response = connection.getDefaultCatalog();
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            CatalogItem item = connection.getCatalogItem(resource.getId());
            assertNotNull(item);
            assertNotNull(item.getEntity());
            assertNotNull(item.getId());
            assertNotNull(item.getLocation());
            assertNotNull(item.getProperties());
            assertNotNull(item.getType());
         }
      }
   }

   @Test
   public void testGetVAppTemplate() throws Exception {
      Catalog response = connection.getDefaultCatalog();
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            CatalogItem item = connection.getCatalogItem(resource.getId());
            if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               assertNotNull(connection.getVAppTemplate(item.getEntity().getId()));
            }
         }
      }
   }

   @Test
   public void testDefaultVDC() throws Exception {
      VDC response = connection.getDefaultVDC();
      assertNotNull(response);
      assertNotNull(response.getId());
      assertNotNull(response.getName());
      assertNotNull(response.getLocation());
      assertNotNull(response.getResourceEntities());
      assertNotNull(response.getAvailableNetworks());
      assertEquals(connection.getVDC(response.getId()).getId(), response.getId());
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.getDefaultTasksList();
      assertNotNull(response);
      assertNotNull(response.getId());
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      assertEquals(connection.getTasksList(response.getId()).getId(), response.getId());
   }

   @Test
   public void testGetTask() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.getDefaultTasksList();
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      if (response.getTasks().size() > 0) {
         Task task = response.getTasks().last();
         assertEquals(connection.getTask(task.getId()).getLocation(), task.getLocation());
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      VDC response = connection.getDefaultVDC();
      for (NamedResource item : response.getResourceEntities().values()) {
         if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
            VApp app = connection.getVApp(item.getId());
            assertNotNull(app);
         }
      }
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint");
      account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      connection = new VCloudContextBuilder("vcloud", new VCloudPropertiesBuilder(URI
               .create(endpoint), account, key).build()).withModules(new Log4JLoggingModule())
               .buildContext().getApi();
   }

}
