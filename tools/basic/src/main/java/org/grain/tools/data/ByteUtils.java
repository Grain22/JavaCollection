package org.grain.tools.data;

/**
 * @author wulifu
 */
public class ByteUtils {

    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) (value & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[0] = (byte) ((value >> 24) & 0xFF);
        return src;
    }

    public static int bytesToInt(byte[] src) {
        return (src[3] & 0xFF) | ((src[2] & 0xFF) << 8) | ((src[1] & 0xFF) << 16) | ((src[0] & 0xFF) << 24);
    }


    private static String byteToHexString(byte b) {
        final String[] hexDigits = {
                "0", "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "a", "b", "c", "d", "e", "f"};
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String bytesToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    public static byte[] getData(byte[] bytes, int begin, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = bytes[begin + i];
        }
        return result;
    }

}
