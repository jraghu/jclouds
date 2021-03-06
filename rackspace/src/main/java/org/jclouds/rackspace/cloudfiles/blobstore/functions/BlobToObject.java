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
package org.jclouds.rackspace.cloudfiles.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObject implements Function<Blob, CFObject> {
   private final ResourceToObjectInfo blob2ObjectMd;
   private final CFObject.Factory objectProvider;

   @Inject
   BlobToObject(ResourceToObjectInfo blob2ObjectMd, CFObject.Factory objectProvider) {
      this.blob2ObjectMd = blob2ObjectMd;
      this.objectProvider = objectProvider;
   }

   public CFObject apply(Blob from) {
      if (from == null)
         return null;
      CFObject object = objectProvider.create(blob2ObjectMd.apply(from.getMetadata()));
      if (from.getContentLength() != null)
         object.setContentLength(from.getContentLength());
      object.setPayload(checkNotNull(from.getPayload(), "payload: " + from));
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}