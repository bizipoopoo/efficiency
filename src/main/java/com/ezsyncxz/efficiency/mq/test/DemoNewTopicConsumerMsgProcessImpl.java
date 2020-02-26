package com.ezsyncxz.efficiency.mq.test;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.ezsyncxz.efficiency.mq.annotation.MQConsumeService;
import com.ezsyncxz.efficiency.mq.entity.MQConsumeResult;
import com.ezsyncxz.efficiency.mq.processor.AbstractMQMsgProcessor;
import java.util.List;

@MQConsumeService(topic="Demo",tags={"*"})
public class DemoNewTopicConsumerMsgProcessImpl extends AbstractMQMsgProcessor {

    @Override
    protected MQConsumeResult consumeMessage(String tag, List<String> keys, MessageExt messageExt) {
        String msg = new String(messageExt.getBody());
        logger.info("获取到的消息为："+msg);
        //TODO 判断该消息是否重复消费（RocketMQ不保证消息不重复，如果你的业务需要保证严格的不重复消息，需要你自己在业务端去重）
        
        //如果注解中tags数据中包含多个tag或者是全部的tag(*)，则需要根据tag判断是那个业务，
        //如果注解中tags为具体的某个tag，则该服务就是单独针对tag处理的
        if(tag.equals("某个tag")){
            //做某个操作
        }
        //TODO 获取该消息重试次数
        int reconsume = messageExt.getReconsumeTimes();
        //根据消息重试次数判断是否需要继续消费
        if(reconsume ==3){//消息已经重试了3次，如果不需要再次消费，则返回成功
            
        }
        MQConsumeResult result = new MQConsumeResult();
        result.setSuccess(true);
        return result;
    }

}