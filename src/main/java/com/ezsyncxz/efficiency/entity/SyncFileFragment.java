package com.ezsyncxz.efficiency.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Arrays;

/**
 * @ClassName SyncFileFragment
 * @Description TODO
 * @Author chenwj
 * @Date 2020/3/2 18:09
 * @Version 1.0
 **/

@Getter
@Setter
@NoArgsConstructor
public class SyncFileFragment {
    private int num;

    private int off;

    private int len;

    private int rollingCheckSum;

    private String md5;

    private byte[] data;

    private SyncFileFragment(Builder builder) {
        setNum(builder.num);
        setOff(builder.off);
        setLen(builder.len);
        setRollingCheckSum(builder.rollingCheckSum);
        setMd5(builder.md5);
        setData(builder.data);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private int num;
        private int off;
        private int len;
        private int rollingCheckSum;
        private String md5;
        private byte[] data;

        private Builder() {
        }

        public Builder num(int val) {
            num = val;
            return this;
        }

        public Builder off(int val) {
            off = val;
            return this;
        }

        public Builder len(int val) {
            len = val;
            return this;
        }

        public Builder rollingCheckSum(int val) {
            rollingCheckSum = val;
            return this;
        }

        public Builder md5(String val) {
            md5 = val;
            return this;
        }

        public Builder data(byte[] val) {
            data = val;
            return this;
        }

        public SyncFileFragment build() {
            return new SyncFileFragment(this);
        }
    }

    @Override
    public String toString() {
        return "SyncFileFragment{" +
                "num=" + num +
                ", off=" + off +
                ", len=" + len +
                ", rollingCheckSum=" + rollingCheckSum +
                ", md5='" + md5 + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
