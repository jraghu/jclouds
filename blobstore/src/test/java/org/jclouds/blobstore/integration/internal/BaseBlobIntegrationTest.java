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
package org.jclouds.blobstore.integration.internal;

import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagDoesntMatch;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifETagMatches;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifModifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.ifUnmodifiedSince;
import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.util.internal.BlobStoreUtilsImpl;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.encryption.EncryptionService.MD5InputStreamResult;
import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
public class BaseBlobIntegrationTest extends BaseBlobStoreIntegrationTest {
   private byte[] oneHundredOneConstitutions;
   private EncryptionService encryptionService;
   private byte[] oneHundredOneConstitutionsMD5;
   private long oneHundredOneConstitutionsLength;

   @BeforeClass(groups = { "integration", "live" })
   @Override
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      encryptionService = Guice.createInjector().getInstance(EncryptionService.class);
      MD5InputStreamResult result = encryptionService.generateMD5Result(getTestDataSupplier()
               .getInput());
      oneHundredOneConstitutions = result.data;
      oneHundredOneConstitutionsMD5 = result.md5;
      oneHundredOneConstitutionsLength = result.length;
      super.setUpResourcesOnThisThread(testContext);
   }

   @SuppressWarnings("unchecked")
   public static InputSupplier<InputStream> getTestDataSupplier() throws IOException {
      byte[] oneConstitution = ByteStreams.toByteArray(new GZIPInputStream(BaseJettyTest.class
               .getResourceAsStream("/const.txt.gz")));
      InputSupplier<ByteArrayInputStream> constitutionSupplier = ByteStreams
               .newInputStreamSupplier(oneConstitution);

      InputSupplier<InputStream> temp = ByteStreams.join(constitutionSupplier);

      for (int i = 0; i < 100; i++) {
         temp = ByteStreams.join(temp, constitutionSupplier);
      }
      return temp;
   }

   @Test(groups = { "integration", "live" })
   public void testBigFileGets() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {
         String key = "constitution.txt";

         uploadConstitution(containerName, key);
         Map<Integer, ListenableFuture<?>> responses = Maps.newHashMap();
         for (int i = 0; i < 10; i++) {

            responses.put(i, Futures.compose(context.getAsyncBlobStore()
                     .getBlob(containerName, key), new Function<Blob, Void>() {

               @Override
               public Void apply(Blob from) {
                  assertEquals(encryptionService.md5(from.getContent()),
                           oneHundredOneConstitutionsMD5);
                  return null;
               }

            }));
         }
         Map<Integer, Exception> exceptions = awaitCompletion(responses, exec, 30000l,
                  Logger.CONSOLE, "get constitution");
         assert exceptions.size() == 0 : exceptions;

      } finally {
         returnContainer(containerName);
      }

   }

   private void uploadConstitution(String containerName, String key) throws IOException {
      Blob sourceObject = context.getBlobStore().newBlob(key);
      sourceObject.getMetadata().setContentType("text/plain");
      sourceObject.getMetadata().setContentMD5(oneHundredOneConstitutionsMD5);
      sourceObject.setContentLength(oneHundredOneConstitutionsLength);
      sourceObject.setPayload(oneHundredOneConstitutions);
      context.getBlobStore().putBlob(containerName, sourceObject);
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfModifiedSince() throws InterruptedException {
      String containerName = getContainerName();
      try {
         String key = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         // first create the object
         addObjectAndValidateContent(containerName, key);
         // now, modify it
         addObjectAndValidateContent(containerName, key);
         Date after = new Date(System.currentTimeMillis() + 1000);

         context.getBlobStore().getBlob(containerName, key, ifModifiedSince(before));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifModifiedSince(after));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(containerName);
      }

   }

   @Test(groups = { "integration", "live" })
   public void testGetIfUnmodifiedSince() throws InterruptedException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         Date before = new Date(System.currentTimeMillis() - 1000);
         addObjectAndValidateContent(containerName, key);
         Date after = new Date(System.currentTimeMillis() + 1000);

         context.getBlobStore().getBlob(containerName, key, ifUnmodifiedSince(after));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifUnmodifiedSince(before));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfMatch() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         String goodETag = addObjectAndValidateContent(containerName, key);

         context.getBlobStore().getBlob(containerName, key, ifETagMatches(goodETag));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifETagMatches("powerfrisbee"));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 412);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetIfNoneMatch() throws InterruptedException, UnsupportedEncodingException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         String goodETag = addObjectAndValidateContent(containerName, key);

         context.getBlobStore().getBlob(containerName, key, ifETagDoesntMatch("powerfrisbee"));
         validateContent(containerName, key);

         try {
            context.getBlobStore().getBlob(containerName, key, ifETagDoesntMatch(goodETag));
            validateContent(containerName, key);
         } catch (HttpResponseException ex) {
            assertEquals(ex.getResponse().getStatusCode(), 304);
         }
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetRange() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         Blob object1 = context.getBlobStore().getBlob(containerName, key, range(0, 5));
         assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(object1), TEST_STRING
                  .substring(0, 6));

         Blob object2 = context.getBlobStore().getBlob(containerName, key,
                  range(6, TEST_STRING.length()));
         assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(object2), TEST_STRING
                  .substring(6, TEST_STRING.length()));
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testGetTwoRanges() throws InterruptedException, IOException {
      String containerName = getContainerName();
      try {

         String key = "apples";

         addObjectAndValidateContent(containerName, key);
         Blob object = context.getBlobStore().getBlob(containerName, key,
                  range(0, 5).range(6, TEST_STRING.length()));

         assertEquals(BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(object), TEST_STRING);
      } finally {
         returnContainer(containerName);
      }
   }

   // @Test(groups = { "integration", "live" })
   // public void testGetTail() throws InterruptedException, ExecutionException, TimeoutException,
   // IOException {
   // String containerName = getContainerName();
   // try {
   //
   // String key = "apples";
   //
   // addObjectAndValidateContent(containerName, key);
   // Blob object = context.getBlobStore().getBlob(containerName, key, tail(5)).get(30,
   // TimeUnit.SECONDS);
   // assertEquals(BlobStoreUtils.getContentAsStringAndClose(object), TEST_STRING
   // .substring(TEST_STRING.length() - 5));
   // assertEquals(object.getContentLength(), 5);
   // assertEquals(object.getMetadata().getSize(), TEST_STRING.length());
   // } finally {
   // returnContainer(containerName);
   // }
   // }

   // @Test(groups = { "integration", "live" })
   // public void testGetStartAt() throws InterruptedException, ExecutionException,
   // TimeoutException,
   // IOException {
   // String containerName = getContainerName();
   // try {
   // String key = "apples";
   //
   // addObjectAndValidateContent(containerName, key);
   // Blob object = context.getBlobStore().getBlob(containerName, key, startAt(5)).get(30,
   // TimeUnit.SECONDS);
   // assertEquals(BlobStoreUtils.getContentAsStringAndClose(object), TEST_STRING.substring(5,
   // TEST_STRING.length()));
   // assertEquals(object.getContentLength(), TEST_STRING.length() - 5);
   // assertEquals(object.getMetadata().getSize(), TEST_STRING.length());
   // } finally {
   // returnContainer(containerName);
   // }
   // }

   private String addObjectAndValidateContent(String sourcecontainerName, String sourceKey)
            throws InterruptedException {
      String eTag = addBlobToContainer(sourcecontainerName, sourceKey);
      validateContent(sourcecontainerName, sourceKey);
      return eTag;
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNotFound() throws InterruptedException {
      String containerName = getContainerName();
      String key = "test";
      try {
         context.getBlobStore().removeBlob(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void objectNotFound() throws InterruptedException {
      String containerName = getContainerName();
      String key = "test";
      try {
         assert !context.getBlobStore().blobExists(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   @DataProvider(name = "delete")
   public Object[][] createData() {
      return new Object[][] { { "normal" }, { "sp ace" }, { "qu?stion" }, { "unic₪de" },
               { "path/foo" }, { "colon:" }, { "asteri*k" }, { "quote\"" }, { "{great<r}" },
               { "lesst>en" }, { "p|pe" } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "delete")
   public void deleteObject(String key) throws InterruptedException {
      String containerName = getContainerName();
      try {
         addBlobToContainer(containerName, key);
         context.getBlobStore().removeBlob(containerName, key);
         assertContainerEmptyDeleting(containerName, key);
      } finally {
         returnContainer(containerName);
      }
   }

   private void assertContainerEmptyDeleting(String containerName, String key) {
      Iterable<? extends StorageMetadata> listing = Iterables.filter(context.getBlobStore().list(
               containerName), new Predicate<StorageMetadata>() {

         @Override
         public boolean apply(StorageMetadata input) {
            return input.getType() == StorageType.BLOB;
         }

      });
      assertEquals(Iterables.size(listing), 0, String.format(
               "deleting %s, we still have %s blobs left in container %s, using encoding %s", key,
               Iterables.size(listing), containerName, LOCAL_ENCODING));
   }

   @Test(groups = { "integration", "live" })
   public void deleteObjectNoContainer() {
      try {
         context.getBlobStore().removeBlob("donb", "test");
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 404);
      } catch (ContainerNotFoundException e) {
      }

   }

   @DataProvider(name = "putTests")
   public Object[][] createData1() throws IOException {

      String realObject = Utils.toStringAndClose(new FileInputStream("pom.xml"));

      return new Object[][] { { "file", "text/xml", new File("pom.xml"), realObject },
               { "string", "text/xml", realObject, realObject },
               { "bytes", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   @Test(groups = { "integration", "live" }, dataProvider = "putTests")
   public void testPutObject(String key, String type, Object content, Object realObject)
            throws InterruptedException, IOException {
      Blob object = context.getBlobStore().newBlob(key);
      object.getMetadata().setContentType(type);
      object.setPayload(Payloads.newPayload(content));
      if (content instanceof InputStream) {
         object.generateMD5();
      }
      String containerName = getContainerName();
      try {
         assertNotNull(context.getBlobStore().putBlob(containerName, object));
         object = context.getBlobStore().getBlob(containerName, object.getMetadata().getName());
         String returnedString = BlobStoreUtilsImpl.getContentAsStringOrNullAndClose(object);
         assertEquals(returnedString, realObject);
         PageSet<? extends StorageMetadata> set = context.getBlobStore().list(containerName);
         assert set.size() == 1 : set;
      } finally {
         returnContainer(containerName);
      }
   }

   @Test(groups = { "integration", "live" })
   public void testMetadata() throws InterruptedException {
      String key = "hello";

      Blob object = context.getBlobStore().newBlob(key);
      object.setPayload(TEST_STRING);
      object.getMetadata().setContentType(MediaType.TEXT_PLAIN);
      object.getMetadata().setSize(new Long(TEST_STRING.length()));
      // NOTE all metadata in jclouds comes out as lowercase, in an effort to normalize the
      // providers.
      object.getMetadata().getUserMetadata().put("Adrian", "powderpuff");
      object.getMetadata().setContentMD5(new JCEEncryptionService().md5(TEST_STRING.getBytes()));
      String containerName = getContainerName();
      try {
         assertNull(context.getBlobStore().blobMetadata(containerName, "powderpuff"));

         addBlobToContainer(containerName, object);
         Blob newObject = validateContent(containerName, key);

         BlobMetadata metadata = newObject.getMetadata();

         validateMetadata(metadata);
         validateMetadata(context.getBlobStore().blobMetadata(containerName, key));

         // write 2 items with the same key to ensure that provider doesn't accept dupes
         object.getMetadata().getUserMetadata().put("Adrian", "wonderpuff");
         object.getMetadata().getUserMetadata().put("Adrian", "powderpuff");

         addBlobToContainer(containerName, object);
         validateMetadata(context.getBlobStore().blobMetadata(containerName, key));

      } finally {
         returnContainer(containerName);
      }
   }

   protected void validateMetadata(BlobMetadata metadata) {
      assert metadata.getContentType().startsWith("text/plain") : metadata.getContentType();
      assertEquals(metadata.getSize(), new Long(TEST_STRING.length()));
      assertEquals(metadata.getUserMetadata().get("adrian"), "powderpuff");
      assertEquals(metadata.getContentMD5(), new JCEEncryptionService().md5(TEST_STRING.getBytes()));
   }

}