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

package org.jclouds.aws.s3.functions;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.S3;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

/**
 * 
 * Depending on your latency and legal requirements, you can specify a location constraint that will
 * affect where your data physically resides.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindRegionToXmlPayload extends BindToStringPayload {

   private final String defaultRegion;

   @Inject
   BindRegionToXmlPayload(@S3 String defaultRegion) {
      this.defaultRegion = defaultRegion;
   }

   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      input = input == null ? defaultRegion : input;
      checkArgument(input instanceof String, "this binder is only valid for Region!");
      String constraint = (String) input;
      String value = null;
      if (Region.US_STANDARD.equals(constraint) || Region.US_EAST_1.equals(constraint)) {
         // nothing to bind as this is default.
         return;
      } else if (Region.EU_WEST_1.equals(constraint))
         value = "EU";
      else if (Region.US_WEST_1.equals(constraint))
         value = "us-west-1";
      else if (Region.AP_SOUTHEAST_1.equals(constraint))
         value = "ap-southeast-1";
      else {
         throw new IllegalStateException("unimplemented location: " + this);
      }
      String payload = String
               .format(
                        "<CreateBucketConfiguration><LocationConstraint>%s</LocationConstraint></CreateBucketConfiguration>",
                        value);
      super.bindToRequest(request, payload);

   }
}
