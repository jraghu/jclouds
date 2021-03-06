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
package org.jclouds.vcloud.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import com.google.inject.name.Names;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudPropertiesBuilder;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code BindCloneVAppParamsToXmlPayload}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.BindCloneVAppParamsToXmlPayloadTest")
public class BindCloneVAppParamsToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         Properties props = new Properties();
         Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(props).build(),
                  "properties"));
      }
   });

   public void testWithDescriptionDeployOn() throws IOException {
      String expected = Utils.toStringAndClose(getClass().getResourceAsStream("/cloneVApp.xml"));
      Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap
               .<String, String> create());
      CloneVAppOptions options = new CloneVAppOptions().deploy().powerOn().withDescription(
               "The description of the new vApp");
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] { options }).atLeastOnce();
      expect(request.getFirstHeaderOrNull("Content-Type")).andReturn(null).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVAppParamsToXmlPayload binder = injector
               .getInstance(BindCloneVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("newName", "new-linux-server");
      map.put("vApp", "https://services.vcloudexpress.terremark.com/api/vapp/201");
      binder.bindToRequest(request, map);
      verify(request);
   }

   public void testDefault() throws IOException {
      String expected = Utils.toStringAndClose(getClass().getResourceAsStream(
               "/cloneVApp-default.xml"));
      Multimap<String, String> headers = Multimaps.synchronizedMultimap(HashMultimap
               .<String, String> create());

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/key")).anyTimes();
      expect(request.getArgs()).andReturn(new Object[] {}).atLeastOnce();
      expect(request.getFirstHeaderOrNull("Content-Type")).andReturn(null).atLeastOnce();
      expect(request.getHeaders()).andReturn(headers).atLeastOnce();
      request.setPayload(expected);
      replay(request);

      BindCloneVAppParamsToXmlPayload binder = injector
               .getInstance(BindCloneVAppParamsToXmlPayload.class);

      Map<String, String> map = Maps.newHashMap();
      map.put("newName", "my-vapp");
      map.put("vApp", "https://services.vcloudexpress.terremark.com/api/vapp/4181");
      binder.bindToRequest(request, map);
      verify(request);
   }
}
