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
package org.jclouds.mezeo.pcs2.binders;

import java.io.File;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BindFileInfoToXmlPayload implements Binder {

   public void bindToRequest(HttpRequest request, Object toBind) {
      PCSFile blob = (PCSFile) toBind;
      String file = String.format(
               "<file><name>%s</name><mime_type>%s</mime_type><public>false</public></file>",
               new File(blob.getMetadata().getName()).getName(), blob.getMetadata().getMimeType());
      request.setPayload(file);
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
               Collections.singletonList(file.getBytes().length + ""));
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               Collections.singletonList("application/vnd.csp.file-info+xml"));
   }
}
