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
package org.jclouds.rackspace.cloudservers.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses a list of {@link String} from a gson string.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseInetAddressListFromJsonResponse extends ParseJson<List<String>> {

   @Inject
   public ParseInetAddressListFromJsonResponse(Gson gson) {
      super(gson);
   }

   Map<String, List<String>> addressMap;

   public List<String> apply(InputStream stream) {
      Type flavor = new TypeToken<Map<String, List<String>>>() {
      }.getType();
      try {
         Map<String, List<String>> map = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
                  flavor);
         return map.values().iterator().next();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}