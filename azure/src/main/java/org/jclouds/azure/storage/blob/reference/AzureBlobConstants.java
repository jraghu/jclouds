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
package org.jclouds.azure.storage.blob.reference;

import org.jclouds.azure.storage.reference.AzureStorageConstants;

/**
 * Configuration properties and constants used in Azure Blob connections.
 * 
 * @author Adrian Cole
 */
public interface AzureBlobConstants extends AzureStorageConstants {
   public static final String PROPERTY_AZUREBLOB_ENDPOINT = "jclouds.azureblob.endpoint";

   /**
    * how long do we wait before obtaining a new timestamp for requests.
    */
   public static final String PROPERTY_AZUREBLOB_SESSIONINTERVAL = "jclouds.azureblob.sessioninterval";


}
