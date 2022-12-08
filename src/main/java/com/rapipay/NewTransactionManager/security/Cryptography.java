package com.rapipay.NewTransactionManager.security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Cryptography {

	private Cryptography() {}	
	public static String encryptByRsa(String data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        try {
	        return RSAEncDec.encryptByRsa(data);
        }catch(Exception e) {
        	return data;
        }
    }
}
