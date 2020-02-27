package com.ezsyncxz.efficiency;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.data.DataCollection;
import com.ezsyncxz.efficiency.redis.RedisUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Iterator;
import java.util.Set;

@SpringBootTest
class EfficiencyApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(EfficiencyApplication.class);

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private DataCollection dataCollection;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void contextLoads() {
    }

    @Test
    public void testMq(){
        String message = "demo msg test";
        logger.info("开始发送消息："+message);
        Message sendMsg = new Message("DemoTopic","DemoTag",message.getBytes());
        //默认3秒超时
        SendResult sendResult = null;
        try {
            sendResult = defaultMQProducer.send(sendMsg, (mqs, msg, arg) -> {
                Integer id = (Integer) arg;
                int index = id % mqs.size();
                return mqs.get(index);
            }, 1);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("消息发送响应信息："+sendResult.toString());
    }

    @Test
    public void testOrderlyMsg(){
        String path = "D:\\chenwj\\dev\\test\\efficiency_src\\mysql-connector-java-5.1.7-bin.zip";
        try {
            dataCollection.collect(path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testZsGet(){
        redisUtil.zsSetAndSorte("demo", "b", 2.0);
        redisUtil.zsSetAndSorte("demo", "a", 2.0);
        redisUtil.zsSetAndSorte("demo", "c", 3.0);
        Set<Object> demo = redisUtil.zsGetDesc("demo");
        Iterator<Object> iterator = demo.iterator();
        while (iterator.hasNext()){
            System.out.println((String) iterator.next());
        }
    }

    @Test
    public void testFileMonitor(){

    }
}
