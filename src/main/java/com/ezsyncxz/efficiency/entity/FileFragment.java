package com.ezsyncxz.efficiency.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @ClassName FileFragment
 * @Description TODO
 * @Author chenwj
 * @Date 2020/2/26 14:01
 * @Version 1.0
 **/

@Getter
@Setter
@NoArgsConstructor
public class FileFragment {

    /**
     * 数据片段
     */
    private byte[] body;

    /**
     * 目标路径
     */
    private String tarPath;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 是否需要解压，默认为否
     */
    private boolean needCompress = false;

    /**
     * 偏移量
     */
    private int off;

    /**
     * 文件大小
     */
    private long length;

    private FileFragment(Builder builder) {
        setBody(builder.body);
        setTarPath(builder.tarPath);
        setFilename(builder.filename);
        setNeedCompress(builder.needCompress);
        setOff(builder.off);
        setLength(builder.length);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private byte[] body;
        private String tarPath;
        private String filename;
        private boolean needCompress;
        private int off;
        private long length;

        private Builder() {
        }

        public Builder body(byte[] val) {
            body = val;
            return this;
        }

        public Builder tarPath(String val) {
            tarPath = val;
            return this;
        }

        public Builder filename(String val) {
            filename = val;
            return this;
        }

        public Builder needCompress(boolean val) {
            needCompress = val;
            return this;
        }

        public Builder off(int val) {
            off = val;
            return this;
        }

        public Builder length(long val) {
            length = val;
            return this;
        }

        public FileFragment build() {
            return new FileFragment(this);
        }
    }
}
