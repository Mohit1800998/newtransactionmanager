package com.rapipay.NewTransactionManager.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.rapipay.security.asym.RSA;
import com.rapipay.security.sym.AES256;
import org.apache.commons.codec.binary.Base64;


public final class Encrypt {

    private Cipher encryptCipher;
    private SecretKey key;
    private IvParameterSpec iv;
    private byte[] ivbyets;
    private byte[] clearText;
    private byte[] encryptedText;

    public static String encryptText(String toEncrypt, String skey, String iv ) throws Exception {

        Encrypt ec = new Encrypt(toEncrypt, skey, iv);
        String entext = ec.toString();
        return entext;
    }

    public Encrypt(String data, String skey, String iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        //Generate the IV and key

        this.ivbyets= Base64.decodeBase64(iv);
        this.iv = new IvParameterSpec(this.ivbyets);
        this.key = new SecretKeySpec(Base64.decodeBase64(skey), "AES");
        this.encryptCipher = createCipher();
        this.clearText = this.convertClearText(data);
        this.encryptedText = this.encrypt();

    }

    private byte[] convertClearText(String clearText) throws UnsupportedEncodingException {

        //Convert the clear text passed by the user into bytes

        return clearText.getBytes("UTF-8");

    }

    private Cipher createCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

        //Create an AES cipher in CBC mode using PKCS5 padding

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        return cipher;

    }

    public IvParameterSpec getIv() {

        return this.iv;

    }

    public String  getIvStr() {

        return Base64.encodeBase64String(this.ivbyets);

    }

    public SecretKey getKey() {

        return this.key;

    }

    private byte[] encrypt() throws IllegalBlockSizeException, BadPaddingException {

        return this.encryptCipher.doFinal(this.clearText);

    }

    @Override

    public String toString() {

        return Base64.encodeBase64String(encryptedText);

    }

}