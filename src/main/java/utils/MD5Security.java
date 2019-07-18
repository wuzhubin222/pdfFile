package utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by saga on 2017/8/10.
 *
 */
public class MD5Security {
    private static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static MessageDigest messagedigest = null;

    public MD5Security() {
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < 16; ++i) {
            int t = bytes[i];
            if(t < 0) {
                t += 256;
            }

            sb.append(hexDigits[t >>> 4]);
            sb.append(hexDigits[t % 16]);
        }

        return sb.toString();
    }

    public static String compute(String inStr) {
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int i = 0; i < charArray.length; ++i) {
            byteArray[i] = (byte)charArray[i];
        }

        byte[] md5Bytes = messagedigest.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();

        for(int i = 0; i < md5Bytes.length; ++i) {
            int val = md5Bytes[i] & 255;
            if(val < 16) {
                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString().toUpperCase();
    }

    public static String md5(String input) throws Exception {
        return code(input, 32);
    }

    public static String code(String input, int bit) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
            return bit == 16?bytesToHex(md.digest(input.getBytes("utf-8"))).substring(8, 24):bytesToHex(md.digest(input.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
            throw new Exception("Could not found MD5 algorithm.", var3);
        }
    }

    public static String md5_3(String b) throws Exception {
        MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
        byte[] a = md.digest(b.getBytes());
        a = md.digest(a);
        a = md.digest(a);
        return bytesToHex(a);
    }

    public static String EncodeMD5Hex(String text) {
        return DigestUtils.md5Hex(text);
    }

    public static void main(String[] args) {
        System.out.println(EncodeMD5Hex("s214587387a"));
        System.out.println(compute("s214587387a"));
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;

        for(int l = m; l < k; ++l) {
            char c0 = hexDigits[(bytes[l] & 240) >> 4];
            char c1 = hexDigits[bytes[l] & 15];
            stringbuffer.append(c0);
            stringbuffer.append(c1);
        }

        return stringbuffer.toString();
    }

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var1) {
            System.err.println("MD5FileUtil messagedigest初始化失");
            var1.printStackTrace();
        }

    }
}
