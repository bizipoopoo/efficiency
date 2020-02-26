package com.ezsyncxz.efficiency.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.impl.consumer.PullResultExt;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.mq.config.MQConsumerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName DataCollectionPullConsumer
 * @Description TODO
 * @Author chenwj
 * @Date 2020/2/26 10:29
 * @Version 1.0
 **/

@Component
public class DataCollectionPullConsumer {

    public static final Logger logger = LoggerFactory.getLogger(MQConsumerConfiguration.class);
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;
    @Value("${rocketmq.consumer.topics}")
    private String topics;
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;
    private static final Map<MessageQueue, Long> offsetTable = new HashMap<MessageQueue, Long>();

    public void pullMsg() {
        offsetTable.clear();
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("pullConsumer");
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        try {
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("DemoTopic");
            for (MessageQueue mq : mqs) {
                logger.warn("当前获取的消息的归属队列是: {}", mq.getQueueId());
                PullResultExt pullResult = (PullResultExt) consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                switch (pullResult.getPullStatus()) {

                    case FOUND:

                        List<MessageExt> messageExtList = pullResult.getMsgFoundList();
                        for (MessageExt m : messageExtList) {
                            System.out.println("收到了消息:" + new String(m.getBody()));
                        }
                        break;

                    case NO_MATCHED_MSG:
                        break;

                    case NO_NEW_MSG:
                        break;

                    case OFFSET_ILLEGAL:
                        break;

                    default:
                        break;
                }
            }
            // }

        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        offsetTable.put(mq, offset);
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = offsetTable.get(mq);
        if (offset != null)
            return offset;
        return 0;
    }

}
