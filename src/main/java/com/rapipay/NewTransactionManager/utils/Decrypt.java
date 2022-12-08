package com.rapipay.NewTransactionManager.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decrypt {

    private IvParameterSpec iv = null;

    public static String decryptText(String skey, String toDecrypt, String iv) throws Exception {

        Decrypt dc = new Decrypt();
        String dctext = dc.DecryptText(skey, toDecrypt, iv);
        return dctext;

    }

    public String DecryptText(String key, String data, String ivs) throws Exception {

        String returndata = null;

        try {

            iv = new IvParameterSpec(Base64.getDecoder().decode(ivs));
            byte[] skey = Base64.getDecoder().decode(key);
            SecretKey sk = new SecretKeySpec(skey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sk, iv);
            byte[] text = cipher.doFinal(Base64.getDecoder().decode(data));
            returndata = new String(text, "UTF-8");

        }

        catch (Exception e) {

            throw e;

        }

        return returndata;

    }

}
