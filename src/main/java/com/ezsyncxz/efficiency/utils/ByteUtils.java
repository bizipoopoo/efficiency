package com.ezsyncxz.efficiency.utils;

/**
 * @ClassName ByteUtils
 * @Description TODO
 * @Author chenwj
 * @Date 2020/2/25 15:36
 * @Version 1.0
 **/

public class ByteUtils {

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static byte[] concateBytes(byte[] arr1, byte[] arr2) {
        byte[] result = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }
}
