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
package org.jclouds.azure.storage.blob.domain;

import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.jclouds.azure.storage.blob.domain.internal.MutableBlobPropertiesImpl;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(MutableBlobPropertiesImpl.class)
public interface MutableBlobProperties extends BlobProperties {
   /**
    * @see ListableContainerProperties#getUrl
    */
   void setUrl(URI url);

   /**
    * @see ListableContainerProperties#getName
    */
   void setName(String name);

   /**
    * @see ListableContainerProperties#getLastModified
    */
   void setLastModified(Date lastModified);

   /**
    * @see ListableContainerProperties#getETag
    */
   void setETag(String eTag);

   /**
    * @see ListableContainerProperties#getContentLength
    */
   void setContentLength(long size);

   /**
    * @see ListableContainerProperties#getContentMD5
    */
   void setContentMD5(byte[] md5);

   /**
    * @see ListableContainerProperties#getContentType
    */
   void setContentType(String contentType);

   /**
    * @see ListableContainerProperties#getContentEncoding
    */
   void setContentEncoding(String contentEncoding);

   /**
    * @see ListableContainerProperties#getContentLanguage
    */
   void setContentLanguage(String contentLanguage);

   /**
    * @see ListableContainerProperties#getMetadata
    */
   void setMetadata(Map<String, String> metadata);

}
