package org.grain.tools.security.rsa;


import javax.naming.OperationNotSupportedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author grain
 */
public class MD5Utils {
    public static final String MD5 = "MD5";
    public static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance(MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * MD5
     * @param bytes 传入数据
     * @return md5 标签结果
     * @throws OperationNotSupportedException md5 初始化失败,操作不可用
     */
    public static synchronized byte[] digest(byte[] bytes) throws OperationNotSupportedException {
        if (messageDigest == null) {
            throw new OperationNotSupportedException("MD5 message digest is not available");
        }
        messageDigest.reset();
        messageDigest.update(bytes);
        return messageDigest.digest();
    }
}
