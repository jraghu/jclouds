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
package org.jclouds.aws.s3.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements Function<ObjectMetadata, MutableBlobMetadata> {
   private final IfDirectoryReturnNameStrategy ifDirectoryReturnName;

   @Inject
   public ObjectToBlobMetadata(IfDirectoryReturnNameStrategy ifDirectoryReturnName) {
      this.ifDirectoryReturnName = ifDirectoryReturnName;
   }

   public MutableBlobMetadata apply(ObjectMetadata from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getContentMD5() != null)
         to.setContentMD5(from.getContentMD5());
      if (from.getContentType() != null)
         to.setContentType(from.getContentType());
      to.setETag(from.getETag());
      to.setName(from.getKey());
      to.setSize(from.getSize());
      to.setLastModified(from.getLastModified());
      to.setUserMetadata(from.getUserMetadata());
      String directoryName = ifDirectoryReturnName.execute(to);
      if (directoryName != null) {
         to.setName(directoryName);
         to.setType(StorageType.RELATIVE_PATH);
      } else {
         to.setType(StorageType.BLOB);
      }
      return to;
   }
}