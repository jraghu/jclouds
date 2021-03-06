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
package org.jclouds.vcloud.terremark.xml;

import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;

/**
 * @author Adrian Cole
 */
public class PublicIpAddressHandler extends HandlerWithResult<PublicIpAddress> {

   @Resource
   protected Logger logger = Logger.NULL;

   private StringBuilder currentText = new StringBuilder();

   private int id;
   private String address;

   private URI location;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   @Override
   public PublicIpAddress getResult() {
      return new PublicIpAddress(id, address, location);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         id = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Href") && currentOrNull() != null) {
         location = URI.create(currentOrNull());
      } else if (qName.equals("Name")) {
         address = currentOrNull();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}