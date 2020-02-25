package com.ezsyncxz.efficiency.mq.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 消费结果
 */
@Getter
@Setter
@NoArgsConstructor
public class MQConsumeResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 是否处理成功
     */
    private boolean isSuccess;
    /**
     * 如果处理失败，是否允许消息队列继续调用，直到处理成功，默认true
     */
    private boolean isReconsumeLater = true;
    /**
     * 是否需要记录消费日志，默认不记录
     */
    private boolean isSaveConsumeLog = false;
    /**
     * 错误Code
     */
    private String errCode;
    /**
     * 错误消息
     */
    private String errMsg;
    /**
     * 错误堆栈
     */
    private Throwable e;

    private MQConsumeResult(Builder builder) {
        setSuccess(builder.isSuccess);
        setReconsumeLater(builder.isReconsumeLater);
        setSaveConsumeLog(builder.isSaveConsumeLog);
        setErrCode(builder.errCode);
        setErrMsg(builder.errMsg);
        setE(builder.e);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private boolean isSuccess;
        private boolean isReconsumeLater;
        private boolean isSaveConsumeLog;
        private String errCode;
        private String errMsg;
        private Throwable e;

        private Builder() {
        }

        public Builder isSuccess(boolean val) {
            isSuccess = val;
            return this;
        }

        public Builder isReconsumeLater(boolean val) {
            isReconsumeLater = val;
            return this;
        }

        public Builder isSaveConsumeLog(boolean val) {
            isSaveConsumeLog = val;
            return this;
        }

        public Builder errCode(String val) {
            errCode = val;
            return this;
        }

        public Builder errMsg(String val) {
            errMsg = val;
            return this;
        }

        public Builder e(Throwable val) {
            e = val;
            return this;
        }

        public MQConsumeResult build() {
            return new MQConsumeResult(this);
        }
    }
}