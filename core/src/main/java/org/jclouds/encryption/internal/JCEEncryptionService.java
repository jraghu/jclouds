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
package org.jclouds.encryption.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
public class JCEEncryptionService extends BaseEncryptionService {

   public String hmacSha256Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      return hmacBase64(toEncode, key, "HmacSHA256");
   }

   private String hmacBase64(String toEncode, byte[] key, String algorithm) {
      byte[] resBuf = hmac(toEncode, key, algorithm);
      return toBase64String(resBuf);
   }

   public byte[] hmac(String toEncode, byte[] key, String algorithm) {
      SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);

      Mac mac = null;
      try {
         mac = Mac.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Could not find the " + algorithm + " algorithm", e);
      }
      try {
         mac.init(signingKey);
      } catch (InvalidKeyException e) {
         throw new RuntimeException("Could not initialize the " + algorithm + " algorithm", e);
      }
      return mac.doFinal(toEncode.getBytes());
   }

   public String hmacSha1Base64(String toEncode, byte[] key) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      return hmacBase64(toEncode, key, "HmacSHA1");
   }

   public byte[] md5(byte[] plainBytes) {
      MessageDigest eTag = getDigest();
      eTag.update(plainBytes, 0, plainBytes.length);
      return eTag.digest();
   }

   public byte[] md5(InputStream toEncode) {
      MessageDigest eTag = getDigest();
      byte[] buffer = new byte[1024];
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               eTag.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(toEncode);
      }
      return eTag.digest();
   }

   public String toBase64String(byte[] resBuf) {
      return Base64.encodeBytes(resBuf, Base64.DONT_BREAK_LINES);
   }

   public MD5InputStreamResult generateMD5Result(InputStream toEncode) {
      MessageDigest eTag = getDigest();
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = toEncode.read(buffer);
            if (numRead > 0) {
               length += numRead;
               eTag.update(buffer, 0, numRead);
               out.write(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(out);
         Closeables.closeQuietly(toEncode);
      }
      return new MD5InputStreamResult(out.toByteArray(), eTag.digest(), length);
   }

   public static MessageDigest getDigest() {
      MessageDigest eTag;
      try {
         eTag = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("Could not find the MD5 algorithm", e);
      }
      return eTag;
   }

   public byte[] fromBase64String(String encoded) {
      return Base64.decode(encoded);
   }

   @Override
   public MD5OutputStream md5OutputStream(OutputStream out) {
      return new JCEMD5OutputStream(out);
   }

   public static class JCEMD5OutputStream extends MD5OutputStream {
      public JCEMD5OutputStream(OutputStream out) {
         super(new DigestOutputStream(out, getDigest()));
      }

      @Override
      public byte[] getMD5() {
         MessageDigest digest = ((DigestOutputStream) out).getMessageDigest();
         return digest.digest();
      }
   }

   @Override
   public String sha1Base64(InputStream plainBytes) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidKeyException {
      MessageDigest sha1 = MessageDigest.getInstance("SHA1");
      byte[] buffer = new byte[1024];
      long length = 0;
      int numRead = -1;
      try {
         do {
            numRead = plainBytes.read(buffer);
            if (numRead > 0) {
               length += numRead;
               sha1.update(buffer, 0, numRead);
            }
         } while (numRead != -1);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         Closeables.closeQuietly(plainBytes);
      }

      return toBase64String(sha1.digest());
   }

   @Override
   public byte[] rsaPrivateEncrypt(String toSign, Key key) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return cipher.doFinal(toSign.getBytes());
   }
}
