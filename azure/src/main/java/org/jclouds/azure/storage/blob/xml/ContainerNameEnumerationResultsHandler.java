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
package org.jclouds.azure.storage.blob.xml;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.BlobType;
import org.jclouds.azure.storage.blob.domain.LeaseStatus;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.internal.BlobPropertiesImpl;
import org.jclouds.azure.storage.blob.domain.internal.HashSetListBlobsResponse;
import org.jclouds.date.DateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * EnumerationResults ContainerName="http://myaccount.blob.core.windows.net/mycontainer"
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135734.aspx#samplerequestandresponse" />
 * @author Adrian Cole
 */
public class ContainerNameEnumerationResultsHandler extends
         ParseSax.HandlerWithResult<ListBlobsResponse> {
   private Set<BlobProperties> blobMetadata = Sets.newLinkedHashSet();
   private String prefix;
   private String marker;
   private int maxResults;
   private String nextMarker;
   private URI currentUrl;
   private URI containerUrl;
   private Date currentLastModified;
   private String currentETag;

   private StringBuilder currentText = new StringBuilder();

   private final EncryptionService encryptionService;
   private final DateService dateParser;
   private String delimiter;
   private String currentName;
   private long currentSize;
   private String currentContentType;
   private String currentContentEncoding;
   private String currentContentLanguage;
   private BlobType currentBlobType;
   private boolean inBlob;
   private boolean inBlobPrefix;
   private boolean inMetadata;
   private Set<String> blobPrefixes = Sets.newHashSet();
   private byte[] currentContentMD5;
   private Map<String, String> currentMetadata = Maps.newHashMap();
   private LeaseStatus currentLeaseStatus;

   @Inject
   public ContainerNameEnumerationResultsHandler(EncryptionService encryptionService,
            DateService dateParser) {
      this.encryptionService = encryptionService;
      this.dateParser = dateParser;
   }

   public ListBlobsResponse getResult() {
      return new HashSetListBlobsResponse(blobMetadata, containerUrl, prefix, marker, maxResults,
               nextMarker, delimiter, blobPrefixes);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("Blob")) {
         inBlob = true;
         inBlobPrefix = false;
         inMetadata = false;
      } else if (qName.equals("BlobPrefix")) {
         inBlob = false;
         inBlobPrefix = true;
      } else if (qName.equals("Metadata")) {
         inBlob = true;
         inMetadata = true;
      } else if (qName.equals("EnumerationResults")) {
         containerUrl = URI.create(attributes.getValue("ContainerName").toString().trim());
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (inMetadata && !qName.equals("Metadata")) {
         currentMetadata.put(qName, currentText.toString().trim());
      } else if (qName.equals("Metadata")) {
         inMetadata = false;
      } else if (qName.equals("MaxResults")) {
         maxResults = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("Marker")) {
         marker = currentText.toString().trim();
         marker = (marker.equals("")) ? null : marker;
      } else if (qName.equals("Prefix")) {
         prefix = currentText.toString().trim();
         prefix = (prefix.equals("")) ? null : prefix;
      } else if (qName.equals("Delimiter")) {
         delimiter = currentText.toString().trim();
         delimiter = (delimiter.equals("")) ? null : delimiter;
      } else if (qName.equals("NextMarker")) {
         nextMarker = currentText.toString().trim();
         nextMarker = (nextMarker.equals("")) ? null : nextMarker;
      } else if (qName.equals("BlobType")) {
         currentBlobType = BlobType.fromValue(currentText.toString().trim());
      } else if (qName.equals("LeaseStatus")) {
         currentLeaseStatus = LeaseStatus.fromValue(currentText.toString().trim());
      } else if (qName.equals("Blob")) {
         BlobProperties md = new BlobPropertiesImpl(currentBlobType, currentName, currentUrl,
                  currentLastModified, currentETag, currentSize, currentContentType,
                  currentContentMD5, currentContentEncoding, currentContentLanguage,
                  currentLeaseStatus, currentMetadata);
         blobMetadata.add(md);
         currentBlobType = null;
         currentName = null;
         currentUrl = null;
         currentLastModified = null;
         currentETag = null;
         currentSize = -1;
         currentContentType = null;
         currentContentEncoding = null;
         currentContentLanguage = null;
         currentContentMD5 = null;
         currentLeaseStatus = null;
         currentMetadata = Maps.newHashMap();
      } else if (qName.equals("Url")) {
         currentUrl = HttpUtils.createUri(currentText.toString().trim());
      } else if (qName.equals("Last-Modified")) {
         currentLastModified = dateParser.rfc822DateParse(currentText.toString().trim());
      } else if (qName.equals("Etag")) {
         currentETag = currentText.toString().trim();
      } else if (qName.equals("Name")) {
         if (inBlob)
            currentName = currentText.toString().trim();
         else if (inBlobPrefix)
            blobPrefixes.add(currentText.toString().trim());
      } else if (qName.equals("Content-Length")) {
         currentSize = Long.parseLong(currentText.toString().trim());
      } else if (qName.equals("Content-MD5")) {
         if (!currentText.toString().trim().equals(""))
            currentContentMD5 = encryptionService.fromBase64String(currentText.toString().trim());
      } else if (qName.equals("Content-Type")) {
         currentContentType = currentText.toString().trim();
      } else if (qName.equals("Content-Encoding")) {
         currentContentEncoding = currentText.toString().trim();
         if (currentContentEncoding.equals(""))
            currentContentEncoding = null;
      } else if (qName.equals("Content-Language")) {
         currentContentLanguage = currentText.toString().trim();
         if (currentContentLanguage.equals(""))
            currentContentLanguage = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
