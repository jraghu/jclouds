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
package org.jclouds.aws.s3.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.internal.CopyObjectResult;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CopyObjectHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.CopyObjectHandlerTest")
public class CopyObjectHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/s3/copy_object.xml");
      ObjectMetadata expected = new CopyObjectResult(new SimpleDateFormatDateService()
               .iso8601DateParse("2009-03-19T13:23:27.000Z"),
               "\"92836a3ea45a6984d1b4d23a747d46bb\"");

      ObjectMetadata result = (ObjectMetadata) factory.create(
               injector.getInstance(CopyObjectHandler.class)).parse(is);

      assertEquals(result, expected);
   }

}
