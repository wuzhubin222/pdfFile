package utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SzcaAESCrypt
{
    
    public static final String keyStr_RSA1024 = "SIGN20161027SCMY";
    
    //public static final String keyStr_YHJR = "YHJR20160726SCMY";
    
    public static final String keyStr_RSA2048 = "GGKH20161202SCMY";
    
    private static final String AESTYPE ="AES/ECB/PKCS5Padding";
    
    public static String AES_Encrypt(String keyStr, String plainText) {
        byte[] encrypt = null;
        try{
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypt = cipher.doFinal(plainText.getBytes());    
        }catch(Exception e){
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(encrypt));
    }
 
    public static String AES_Decrypt(String keyStr, String encryptData) {
        byte[] decrypt = null;
        try{
            Key key = generateKey(keyStr);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(Base64.decodeBase64(encryptData.getBytes()));
        }catch(Exception e){
            e.printStackTrace();
        }
        return new String(decrypt).trim();
    }
 
    private static Key generateKey(String key)throws Exception{
        try{           
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            return keySpec;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
 
    }
    
    public static void main(String[] args) {
        String plainText = "123456";
         
        /*String encText = AES_Encrypt(keyStr_RWY, plainText);
        String decString = AES_Decrypt(keyStr, encText);
         
        System.out.println(encText);
        System.out.println(decString);*/
        
//        String encText = AES_Encrypt(keyStr_RSA2048, plainText);
//        System.out.println(encText);
        
        
        String decString = AES_Decrypt(keyStr_RSA2048, "v2jpg9IosgU7jmFBLNI96A==");
        //String decString = AES_Decrypt(keyStr_RSA1024, "gYuKXzEvtoHbhn6KyfT8ag==");
        System.out.println(decString);
        
    }
}
