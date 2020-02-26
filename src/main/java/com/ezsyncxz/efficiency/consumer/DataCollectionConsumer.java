package com.ezsyncxz.efficiency.consumer;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.ezsyncxz.efficiency.mq.annotation.MQConsumeService;
import com.ezsyncxz.efficiency.mq.entity.MQConsumeResult;
import com.ezsyncxz.efficiency.mq.processor.AbstractMQMsgProcessor;
import com.ezsyncxz.efficiency.utils.ByteUtils;
import com.ezsyncxz.efficiency.utils.FileUtils;

import java.util.List;

/**
 * @ClassName DataCollectionConsumer
 * @Description TODO
 * @Author chenwj
 * @Date 2020/2/25 15:49
 * @Version 1.0
 **/

@MQConsumeService(topic = "DemoTopic", tags = {"*"})
public class DataCollectionConsumer extends AbstractMQMsgProcessor {


    @Override
    protected MQConsumeResult consumeMessage(String tag, List<String> keys, MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String common = new String(body);
        byte[] message = new byte[0];
        // 没有读到终止符就继续读取
        while (!common.equals("EOF")) {
            message = ByteUtils.concateBytes(message, body);
        }

        // 读取完毕，写入磁盘
        FileUtils.byte2File(body, "D:\\chenwj\\dev\\test\\efficiency_tar", "java-client-0.1.2.zip");
        MQConsumeResult result = new MQConsumeResult();
        result.setSuccess(true);
        return result;
    }
}
