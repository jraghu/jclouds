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
package org.jclouds.nirvanix.sdn.config;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.nirvanix.sdn.SDN;
import org.jclouds.nirvanix.sdn.SDNAuthentication;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.AsyncClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the SDN authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RestSDNAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @SDN
   protected URI provideAuthenticationURI(@Named(SDNConstants.PROPERTY_SDN_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @SessionToken
   protected String provideSessionToken(AsyncClientFactory factory,
            @Named(SDNConstants.PROPERTY_SDN_APPKEY) String appKey,
            @Named(SDNConstants.PROPERTY_SDN_USERNAME) String username,
            @Named(SDNConstants.PROPERTY_SDN_PASSWORD) String password)
            throws InterruptedException, ExecutionException, TimeoutException {
      return factory.create(SDNAuthentication.class).authenticate(appKey, username, password).get(
               20, TimeUnit.SECONDS);
   }

}