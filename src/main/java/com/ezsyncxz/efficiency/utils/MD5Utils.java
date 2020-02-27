package com.ezsyncxz.efficiency.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName MD5Utils
 * @Description TODO
 * @Author chenwj
 * @Date 2020/2/26 14:08
 * @Version 1.0
 **/

public class MD5Utils {

    private static final String HEX_DIGITS = "0123456789abcdef";

    private static final int BYTE_MSK = 0xFF;

    private static final int HEX_DIGIT_MASK = 0xF;

    private static final int HEX_DIGIT_BITS = 4;

    public static String MD54bytes(byte[] arg) throws UnsupportedOperationException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(arg);
            byte[] res = md.digest();
            return toHexString(res);
        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    private static String toHexString(final byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++) {
            int b = byteArray[i] & BYTE_MSK;
            sb.append(HEX_DIGITS.charAt(b >>> HEX_DIGIT_BITS)).append(
                    HEX_DIGITS.charAt(b & HEX_DIGIT_MASK));
        }
        return sb.toString();
    }
}
