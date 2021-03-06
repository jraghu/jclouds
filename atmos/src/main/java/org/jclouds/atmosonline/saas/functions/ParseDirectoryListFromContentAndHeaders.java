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
package org.jclouds.atmosonline.saas.functions;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.internal.BoundedHashSet;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.atmosonline.saas.xml.ListDirectoryResponseHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;

import com.google.common.base.Function;

/**
 * This parses {@link BoundedSet} from HTTP headers and xml content.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseDirectoryListFromContentAndHeaders implements
         Function<HttpResponse, BoundedSet<DirectoryEntry>> {

   private final ParseSax.Factory factory;
   private final Provider<ListDirectoryResponseHandler> listHandlerProvider;

   @Inject
   private ParseDirectoryListFromContentAndHeaders(Factory factory,
            Provider<ListDirectoryResponseHandler> orgHandlerProvider) {
      this.factory = factory;
      this.listHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link BoundedSet} object.
    */
   public BoundedSet<DirectoryEntry> apply(HttpResponse from) {
      String token = from.getFirstHeaderOrNull(AtmosStorageHeaders.TOKEN);
      return new BoundedHashSet<DirectoryEntry>(factory.create(listHandlerProvider.get()).parse(
               from.getContent()), token);
   }
}
