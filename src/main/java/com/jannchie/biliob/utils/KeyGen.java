package com.jannchie.biliob.utils;

import org.apache.shiro.codec.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

/**
 * @author jannchie
 */
public class KeyGen {
  public String getKey(){
    try {
      KeyGenerator keygen = KeyGenerator.getInstance("AES");
      SecretKey key = keygen.generateKey();
      return Base64.encodeToString(key.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }
}
