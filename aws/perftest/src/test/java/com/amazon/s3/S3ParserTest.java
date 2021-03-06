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
package com.amazon.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

/**
 * Compares performance of xml parsing apis.
 * 
 * @author Adrian Cole
 */
@Test(sequential = true, timeOut = 2 * 60 * 1000, testName = "s3.S3ParserTest")
public class S3ParserTest extends org.jclouds.aws.s3.xml.S3ParserTest {

   class MockHttpURLClient extends HttpURLConnection {
      private String content;

      @Override
      public InputStream getInputStream() throws IOException {
         return org.jclouds.util.Utils.toInputStream(content);
      }

      protected MockHttpURLClient(String content) {
         super(null);
         this.content = content;
      }

      public void disconnect() {
      }

      public boolean usingProxy() {
         return false;
      }

      @Override
      public int getResponseCode() throws IOException {
         return 200;
      }

      public void connect() throws IOException {
      }
   }

   @Test
   void testAmazonParseListAllMyBucketsSerialResponseTime() throws IOException {
      for (int i = 0; i < LOOP_COUNT; i++)
         runAmazonParseListAllMyBuckets();
   }

   @Test
   void testAmazonParseListAllMyBucketsParallelResponseTime() throws InterruptedException,
            ExecutionException {
      CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);

      for (int i = 0; i < LOOP_COUNT; i++)
         completer.submit(new Callable<Boolean>() {
            public Boolean call() throws IOException {
               runAmazonParseListAllMyBuckets();
               return true;
            }
         });
      for (int i = 0; i < LOOP_COUNT; i++)
         assert completer.take().get();
   }

   @SuppressWarnings("unchecked")
   @Test(enabled = false)
   public void testAmazonCanParseListAllMyBuckets() throws IOException {
      ListAllMyBucketsResponse response = runAmazonParseListAllMyBuckets();
      List<Bucket> buckets = response.entries;
      Bucket bucket1 = (Bucket) buckets.get(0);
      assert bucket1.name.equals("adrianjbosstest");
      Date expectedDate1 = new SimpleDateFormatDateService()
               .iso8601DateParse("2009-03-12T02:00:07.000Z");
      Date date1 = bucket1.creationDate;
      assert date1.toString().equals(expectedDate1.toString());
      Bucket bucket2 = (Bucket) buckets.get(1);
      assert bucket2.name.equals("adrianjbosstest2");
      Date expectedDate2 = new SimpleDateFormatDateService()
               .iso8601DateParse("2009-03-12T02:00:09.000Z");
      Date date2 = bucket2.creationDate;
      assert date2.toString().equals(expectedDate2.toString());
      assert buckets.size() == 2;
   }

   private ListAllMyBucketsResponse runAmazonParseListAllMyBuckets() throws IOException {
      ListAllMyBucketsResponse response = new ListAllMyBucketsResponse(new MockHttpURLClient(
               listAllMyBucketsResultOn200));
      return response;
   }

   @Test(enabled = false)
   public void testAmazonCanParseListBucketResult() throws IOException {
      ListBucketResponse response = runAmazonParseListBucketResult();
      ListEntry content = (ListEntry) response.entries.get(0);
      assert content.key.equals("3366");
      assert content.lastModified.equals(new SimpleDateFormatDateService()
               .iso8601DateParse("2009-03-12T02:00:13.000Z"));
      assert content.eTag.equals("\"9d7bb64e8e18ee34eec06dd2cf37b766\"");
      assert content.size == 136;
      assert content.owner.id
               .equals("e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0");
      assert content.owner.displayName.equals("ferncam");
      assert content.storageClass.equals("STANDARD");
   }

   private ListBucketResponse runAmazonParseListBucketResult() throws IOException {
      ListBucketResponse response = new ListBucketResponse(new MockHttpURLClient(
               listAllMyBucketsResultOn200));
      return response;
   }

   @Test(enabled = false)
   void testAmazonParseListBucketResultSerialResponseTime() throws IOException {
      for (int i = 0; i < LOOP_COUNT; i++)
         runAmazonParseListBucketResult();
   }

   @Test(enabled = false)
   void testAmazonParseListBucketResultParallelResponseTime() throws InterruptedException,
            ExecutionException {
      CompletionService<Boolean> completer = new ExecutorCompletionService<Boolean>(exec);

      for (int i = 0; i < LOOP_COUNT; i++)
         completer.submit(new Callable<Boolean>() {
            public Boolean call() throws IOException {
               runAmazonParseListBucketResult();
               return true;
            }
         });
      for (int i = 0; i < LOOP_COUNT; i++)
         assert completer.take().get();
   }

}
