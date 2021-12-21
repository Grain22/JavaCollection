package org.grain.tools.data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.grain.tools.data.ByteUtils.bytesToHexString;

/**
 * @author laowu
 * @version demo
 * 2019/7/3 17:34
 */
public class DigestUtil {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";

    public static String sha1Digests(String input) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance(SHA1);
            resultString = bytesToHexString(md.digest(stringByte(input)));
        } catch (Exception ignore) {
        }
        return resultString;
    }

    public static String sha256Digests(String input) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256);
            resultString = bytesToHexString(md.digest(stringByte(input)));
        } catch (Exception ignore) {
        }
        return resultString;
    }

    public static String md5Digests(String input) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            resultString = bytesToHexString(md.digest(stringByte(input)));
        } catch (Exception ignore) {
        }
        return resultString;
    }

    private static byte[] stringByte(String input) {
        return input.trim().getBytes(StandardCharsets.UTF_8);
    }
}
