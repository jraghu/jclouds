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
package org.jclouds.azure.storage.reference;

/**
 * Configuration properties and constants used in Azure Storage connections.
 * 
 * @author Adrian Cole
 */
public interface AzureStorageConstants {
   public static final String PROPERTY_AZURESTORAGE_ACCOUNT = "jclouds.azure.storage.account";
   public static final String PROPERTY_AZURESTORAGE_KEY = "jclouds.azure.storage.key";
   /**
    * how long do we wait before obtaining a new timestamp for requests.
    */
   public static final String PROPERTY_AZURESTORAGE_SESSIONINTERVAL = "jclouds.azure.storage.sessioninterval";
}
