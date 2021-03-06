/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.boxdotnet;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.boxdotnet.config.BoxDotNetRestClientModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests annotation parsing of {@code BoxDotNetAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "boxdotnet.BoxDotNetAsyncClientTest")
public class BoxDotNetAsyncClientTest extends RestClientTest<BoxDotNetAsyncClient> {


   public void testList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = BoxDotNetAsyncClient.class.getMethod("list");
      GeneratedHttpRequest<BoxDotNetAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://www.box.net/api/1.0/rest/items HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://www.box.net/api/1.0/rest/items HTTP/1.1");
      // for example, using basic authentication, we should get "only one" header
      assertHeadersEqual(httpRequest, "Accept: application/json\nAuthorization: Basic dXNlcjprZXk=\n");
      assertPayloadEquals(httpRequest, null);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testGet() throws SecurityException, NoSuchMethodException, IOException {
      Method method = BoxDotNetAsyncClient.class.getMethod("get", long.class);
      GeneratedHttpRequest<BoxDotNetAsyncClient> httpRequest = processor.createRequest(method, 1);

      assertRequestLineEquals(httpRequest, "GET https://www.box.net/api/1.0/rest/items/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      // TODO: insert expected response class, which probably extends ParseJson
      assertResponseParserClassEquals(method, httpRequest, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      // note that get methods should convert 404's to null
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDelete() throws SecurityException, NoSuchMethodException, IOException {
      Method method = BoxDotNetAsyncClient.class.getMethod("delete", long.class);
      GeneratedHttpRequest<BoxDotNetAsyncClient> httpRequest = processor.createRequest(
               method, 1);

      assertRequestLineEquals(httpRequest,
               "DELETE https://www.box.net/api/1.0/rest/items/1 HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
   @Override
   protected void checkFilters(GeneratedHttpRequest<BoxDotNetAsyncClient> httpRequest) {
      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<BoxDotNetAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<BoxDotNetAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new BoxDotNetRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new BoxDotNetPropertiesBuilder("user", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }
      };
   }
}
