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
package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Meredith
 */

@Singleton
public class ParseDestroyResponseFromJsonResponse extends ParseJson<List<String>> {
   @Inject
   public ParseDestroyResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   private static class DestroyResponse extends RimuHostingResponse {
      private List<String> cancel_messages;

      public List<String> getCancelMessages() {
         return cancel_messages;
      }

      @SuppressWarnings("unused")
      public void setCancelMessages(List<String> cancel_messages) {
         this.cancel_messages = cancel_messages;
      }
   }

   @Override
   protected List<String> apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, DestroyResponse>>() {
      }.getType();
      try {
         Map<String, DestroyResponse> responseMap = gson.fromJson(new InputStreamReader(stream,
                  "UTF-8"), setType);
         return responseMap.values().iterator().next().getCancelMessages();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}