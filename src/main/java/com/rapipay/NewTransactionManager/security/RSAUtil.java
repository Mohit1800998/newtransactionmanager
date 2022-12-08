package com.rapipay.NewTransactionManager.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
public class RSAUtil {
	
	    
	public static String publicKey =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjmpuIeCHZt0MxQCcZCtohbFazpAboVJZ2DnSmacUH9lULM2an/FZIQwvKP2fMtK0afihMJql+9amJgiRV2dhbSoUdUebj6r9AeUp7f7s+UH+oEm8zEWL7v1aU5gF+xUYk5zFnHSD7xmTaVI9bJ49IfkpyKjrDKsVmuXFyQZs/W6X9baTuH91jnvJH1CqlJxUA3Z5udB2cvFK21gV3ltiYRmlqdvUHNcNTFPirN6+ZijtWLr8INFMU5Z8GvUYi4wGWk+o1lRGWkmBd+F8AaZ+FHKMhAAatRjjSZ/QHNjA1mw/MtgJnl+pkK/YBT8boozxva1RB5pT23YnnJJIkk/+4wIDAQAB";
	public static  String privateKey="MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCOam4h4Idm3QzFAJxkK2iFsVrOkBuhUlnYOdKZpxQf2VQszZqf8VkhDC8o/Z8y0rRp+KEwmqX71qYmCJFXZ2FtKhR1R5uPqv0B5Snt/uz5Qf6gSbzMRYvu/VpTmAX7FRiTnMWcdIPvGZNpUj1snj0h+SnIqOsMqxWa5cXJBmz9bpf1tpO4f3WOe8kfUKqUnFQDdnm50HZy8UrbWBXeW2JhGaWp29Qc1w1MU+Ks3r5mKO1Yuvwg0UxTlnwa9RiLjAZaT6jWVEZaSYF34XwBpn4UcoyEABq1GONJn9Ac2MDWbD8y2AmeX6mQr9gFPxuijPG9rVEHmlPbdieckkiST/7jAgMBAAECggEAVOCjoLcjm6+005ionJ6uXbe1ChUjEoN1HoRfusm0GcM6ArXLKPX7i9GqLcDyZ6Tid6IB12jj8fMc9N/4V2I604fXx4oU8YwT3FRLKBfd/jRR4A/yYMA2RQpkEhwxvFH3Ysp1j+h1VUQl8ghcqWapwpywRXI/HD6+0qGMvmGBBY1HhpfByh6RThPxpvoydUNRmmtAGhOxU8gdlrtYgebOLXdiBENwt9vHMVVMcSkRO+uy/l9AiK5YEMssitdi1N9XLqXLKuOU4h96PpwM+XmZgbRdFHNr358pcNHXssqu58TGG2WdGzQzr7rDy9/CPn2t4vyJBsD62ELXHN+w9aFyyQKBgQDDKpx2cSbUlxDa2mCOH8HsLpmqZSLRDEAsLz5VeHtdgWLmwJS9MW7ERvwaH4EGhlq5tAON4ToihqrICnDnjmSyaOHvk3WwzxcUx64/w+8AZgeto5QDq8RxdZUVJyIm9kus3o9JusfTnDRa0dAzHHeqcRUUzJYGP+71/6MJpsEEBQKBgQC6zo3g46Q93Oky7MdO+R48WkzZrX0n04wm2lc/eAqOIUCGUx/0YIVYnn3K6KE6+J68o3nmi10+3wxLjtKU4tgPqw50BlzkMiftzHQLvJUYPXutTU9pzWyiv7V/Czi8h/Sy3PQ0LLP668uNM7l7JwPf6mdT7bd4lpuj8jZdycuTxwKBgQCdT4ZoLdJnvxBV53m2dUChN/qijvyeVeqP8tCiogeGGhl297msOeEXkkP4ZnZgpQER+Fh5DxcF51hW/5t7+ZmO+N1Md6aAipwHIuCAA1VtE9CSlGxk/RnNfkJZl35+uz3KLGaAvm39UkYdAkt4NzT5jEa6yiEPDo91d8WZrPCdZQKBgBZNMoL2qlT4Wp7JxcGX3BQ4c5RJMfffOYwp5OCe5COztJBUvCvgmgrKYRooS5dOtySuH492c9tdpYofsuQzcLfuI/uWBuD3W+z3y6LbELGkJW+7Gw/2hlGnsXNJU6yuTnhMCJtx+sqp+9MPqcUaE0xYH53Vc2HiGRKph/QKc1NhAoGAaB0rVngvfoz98vWmsm4WtN2jpJQ0sA7xvIFxXP1fntgddjdz3X1Dya3HHGmdk+ybxgeB5I1QS8sijFNwV37+E+x1KL0fUJza9T4jnAh5nYu+ntnD9o2RVDa2vb+5/htXH75TV20TRpCCKigQXmTyv3cVk7DpUMLhskr/oXr0RKk="; 
		public static PublicKey getPublicKey(String base64PublicKey){
		        PublicKey publicKey = null;
		        try{
		            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
		            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		            publicKey = keyFactory.generatePublic(keySpec);
		            return publicKey;
		        } catch (NoSuchAlgorithmException e) {
		            e.printStackTrace();
		        } catch (InvalidKeySpecException e) {
		            e.printStackTrace();
		        }
		        return publicKey;
		    }

		    public static PrivateKey getPrivateKey(String base64PrivateKey){
		        PrivateKey privateKey = null;
		        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
		        KeyFactory keyFactory = null;
		        try {
		            keyFactory = KeyFactory.getInstance("RSA");
		        } catch (NoSuchAlgorithmException e) {
		            e.printStackTrace();
		        }
		        try {
		            privateKey = keyFactory.generatePrivate(keySpec);
		        } catch (InvalidKeySpecException e) {
		            e.printStackTrace();
		        }
		        return privateKey;
		    }

		    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
		        return cipher.doFinal(data.getBytes());
		    }

		    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
		        cipher.init(Cipher.DECRYPT_MODE, privateKey);
		        return new String(cipher.doFinal(data));
		    }

		    public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
		    }

		    
		    public static String  dec(String data) {
		    	try {
					return RSAUtil.decrypt(data, privateKey);
				} catch (Exception e) {
					
					
				}
		    	return "Error in decryption";
		    }
}
