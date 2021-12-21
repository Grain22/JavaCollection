package org.grain.tools.security.rsa;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author wulifu
 */
public class RsaUtils {
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String KEY_ALGORITHM = "RSA";

    public static KeyPair generator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] sign(byte[] data, PrivateKey key) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM);
        instance.initSign(key);
        instance.update(data);
        return instance.sign();
    }

    public static boolean verify(byte[] bytes, byte[] signed, PublicKey publicKey) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
        verifySign.initVerify(publicKey);
        verifySign.update(bytes);
        return verifySign.verify(signed);
    }

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decode(String string) {
        return Base64.getDecoder().decode(string);
    }

    public static String getPrivateKeyString(PrivateKey privateKey) {
        return encode(privateKey.getEncoded());
    }

    public static String getPublicKeyString(PublicKey publicKey) {
        return encode(publicKey.getEncoded());
    }

    public static RSAPublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    public static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
}
