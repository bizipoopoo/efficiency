package com.ezsyncxz.efficiency.rsync;

import com.ezsyncxz.efficiency.constants.Constant;
import com.ezsyncxz.efficiency.entity.SyncFileFragment;
import com.ezsyncxz.efficiency.utils.ByteUtils;
import com.ezsyncxz.efficiency.utils.MD5Utils;

import java.io.*;
import java.util.*;

/**
 * @ClassName Rsync
 * @Description TODO
 * @Author chenwj
 * @Date 2020/3/2 14:15
 * @Version 1.0
 **/

public class Rsync {

    public static int adler32(byte[] data, int offset, int length) {
        int a = 0;
        int b = 0;

        for (int i = offset, limit = i + length; i < limit; i++) {
            a += data[i] & 0xff;
            a %= Constant.MOD_ADLER;
            b += a;
            b %= Constant.MOD_ADLER;
        }

        return b << 16 | a;
    }

    public static int adler32(byte[] data, int length) {
        return adler32(data, 0, length);
    }

    /**
     * @param data
     * @return
     */
    public static int adler32(byte[] data) {
        return adler32(data, 0, data.length);
    }

    /**
     * @param oldAdler32
     * @param preByte
     * @param nextByte
     * @return
     */
    public static int nextAdler32(int oldAdler32, byte preByte, byte nextByte) {
        int a = oldAdler32 & 0xffff;
        int b = (oldAdler32 >>> 16) & 0xffff;

        int an = a - preByte + nextByte;

        int bn = b - (preByte) * Constant.TRUNCK_SIZE + an;

        return (bn << 16) + (an & 0xffff);
    }

    public static void main(String[] args) throws IOException {

        String src = "D:\\chenwj\\dev\\test\\efficiency_src\\rsync测试.txt";
        String tar = "D:\\chenwj\\dev\\test\\efficiency_tar\\rsync测试.txt";
        String copy = "D:\\chenwj\\dev\\test\\efficiency_copy\\rsync测试.txt";
        RandomAccessFile f1 = new RandomAccessFile(src, "r");
        RandomAccessFile f2 = new RandomAccessFile(tar, "r");

        List<SyncFileFragment> sff1 = new ArrayList<>();
        List<SyncFileFragment> sff2 = new ArrayList<>();
        byte[] buff = new byte[512];
        int off = 0;
        int len;
        int i = 0;
        Map<String, SyncFileFragment> map = new HashMap<>();
        Map<Integer, byte[]> dataMap = new HashMap<>();
        while ((len = f2.read(buff)) > 0) {
            SyncFileFragment fragment = SyncFileFragment.newBuilder()
                    .len(len)
                    .md5(MD5Utils.MD54bytes(buff))
                    .num(i++)
                    .off(off)
                    .data(ByteUtils.concateBytes(buff, new byte[0]))
                    .rollingCheckSum(adler32(buff))
                    .build();
            sff2.add(fragment);
            map.put(Integer.toHexString(fragment.getRollingCheckSum()), fragment);
            dataMap.put(fragment.getNum(), fragment.getData());
            off += len;
        }

        boolean reset = true;
        int adler32 = 0;
        byte preByte = 0; // 上一个byte
        ByteArrayOutputStream noMatchBuffer = new ByteArrayOutputStream(4 * 1024);
        int j;
        for (j = 0; (len = f1.read(buff)) > 0;) {
            if(j == 45592) {
                System.out.println();
            }
//            adler32 = reset || f1.getFilePointer() == f1.length() ? adler32(buff) : nextAdler32(adler32, preByte, buff[len-1]);
            adler32 = adler32(buff);
            SyncFileFragment fileFragment;
            if((fileFragment = map.get(Integer.toHexString(adler32))) != null && fileFragment.getRollingCheckSum() == adler32 && fileFragment.getMd5().equals(MD5Utils.MD54bytes(buff))) { // match
                // 判断之前是否有未匹配的缓存，统一写入列表
                byte[] noMatchBytes = noMatchBuffer.toByteArray();
                if(noMatchBytes.length > 0) { // 缓冲区有数据
                    SyncFileFragment fragment = SyncFileFragment.newBuilder()
                            .rollingCheckSum(adler32(noMatchBytes))
                            .data(noMatchBytes)
                            .off(j - noMatchBytes.length)
                            .md5(MD5Utils.MD54bytes(noMatchBytes))
                            .len(noMatchBytes.length)
                            .num(sff1.size())
                            .build();
                    sff1.add(fragment);
                    noMatchBuffer.reset();
                }
                // 记录匹配块
                sff1.add(SyncFileFragment.newBuilder()
                        .len(fileFragment.getLen())
                        .md5(fileFragment.getMd5())
                        .rollingCheckSum(adler32)
                        .num(fileFragment.getNum())
                        .off(j)
                        .build());
                reset = true;
                // 移动指针到下一个块
                f1.seek(j + len);
                j = j + len;
            } else { // no match
                reset = false;
                // 未匹配的第一个字节写入缓冲区
                preByte = buff[0];
                noMatchBuffer.write(preByte);
                // 继续滚动
                j += 1;
                f1.seek(j);
            }
        }
        byte[] noMatchBytes = noMatchBuffer.toByteArray();
        if(noMatchBytes.length > 0) { // 缓冲区有数据
            SyncFileFragment fragment = SyncFileFragment.newBuilder()
                    .rollingCheckSum(adler32(noMatchBytes))
                    .off(j - noMatchBytes.length)
                    .md5(MD5Utils.MD54bytes(noMatchBytes))
                    .len(noMatchBytes.length)
                    .data(noMatchBytes)
                    .num(sff1.size())
                    .build();
            sff1.add(fragment);
        }
//        sff2.forEach(System.out::println);
//        sff1.forEach(System.out::println);

        File file = new File(copy);
        if(!file.exists()){
            file.createNewFile();
        }
        RandomAccessFile f3 = new RandomAccessFile(copy, "rw");
        for (SyncFileFragment syncFileFragment : sff1) {
            int num = syncFileFragment.getNum();
            int off1 = syncFileFragment.getOff();
            byte[] data = syncFileFragment.getData();
            int len1 = syncFileFragment.getLen();
            if(data == null) {
                data = dataMap.get(num);
            }
            data = ByteUtils.subBytes(data, 0, len1);
            f3.seek(off1);
            f3.write(data);
        }
        f1.close();
        f2.close();
        f3.close();
    }
}
