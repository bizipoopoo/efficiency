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
     * 消息总数
     */
    private int msgCount;

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
     * 分数
     */
    private double score;

    private FileFragment(Builder builder) {
        setBody(builder.body);
        setMsgCount(builder.msgCount);
        setTarPath(builder.tarPath);
        setFilename(builder.filename);
        setNeedCompress(builder.needCompress);
        setScore(builder.score);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private byte[] body;
        private int msgCount;
        private String tarPath;
        private String filename;
        private boolean needCompress;
        private double score;

        private Builder() {
        }

        public Builder body(byte[] val) {
            body = val;
            return this;
        }

        public Builder msgCount(int val) {
            msgCount = val;
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

        public Builder score(double val) {
            score = val;
            return this;
        }

        public FileFragment build() {
            return new FileFragment(this);
        }
    }
}
