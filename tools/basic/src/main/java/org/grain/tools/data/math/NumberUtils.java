package org.grain.tools.data.math;

import javax.naming.OperationNotSupportedException;
import java.math.BigInteger;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * 数字工具类
 *
 * @author wulifu
 */
public class NumberUtils {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String DIGIT_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/";
    public static final int DIGIT_LENGTH = DIGIT_STRING.length();
    private static final char[] DIGIT_CHARS = DIGIT_STRING.toCharArray();
    private static final BigInteger INTEGER0 = BigInteger.ZERO;
    private static final Logger logger = Logger.getLogger(NumberUtils.class.getName());
    /**
     * 10进制转任意进制
     */
    public static String numToRadix(String number, int radix) throws OperationNotSupportedException {
        if (radix < 2 || radix > DIGIT_LENGTH) {
            logger.warning("radix is not in [2,64]");
            throw new OperationNotSupportedException();
        }
        BigInteger bigNumber = new BigInteger(number);
        BigInteger bigRadix = new BigInteger(radix + "");
        Stack<Character> stack = new Stack<>();
        StringBuilder result = new StringBuilder(0);
        while (!bigNumber.equals(INTEGER0)) {
            stack.add(DIGIT_CHARS[bigNumber.remainder(bigRadix).intValue()]);
            bigNumber = bigNumber.divide(bigRadix);
        }
        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }
        return result.length() == 0 ? "0" : result.toString();
    }

    /**
     * 任意进制转10进制
     */
    public static String radixToNum(String number, int radix) throws OperationNotSupportedException {
        if (radix < 2 || radix > DIGIT_LENGTH) {
            logger.warning("radix is not in [2,64]");
            throw new OperationNotSupportedException();
        }
        if (radix == 10) {
            return number;
        }
        char[] ch = number.toCharArray();
        int len = ch.length;
        BigInteger bigRadix = new BigInteger(radix + "");
        BigInteger result = new BigInteger("0");
        BigInteger base = new BigInteger("1");
        for (int i = len - 1; i >= 0; i--) {
            BigInteger index = new BigInteger(DIGIT_STRING.indexOf(ch[i]) + "");
            result = result.add(index.multiply(base));
            base = base.multiply(bigRadix);
        }
        return result.toString();
    }
}
