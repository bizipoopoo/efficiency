package com.ezsyncxz.efficiency.data;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.utils.ByteUtils;
import com.ezsyncxz.efficiency.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;

/**
 * @ClassName DataCollection
 * @Description 采集指定文件夹数据，将数据读取，归档，分块写入消息队列中，rocketMq能够存入的消息大小最大为4MB，因此我们需要对过大的数据进行切割
 * @Author chenwj
 * @Date 2020/2/24 15:46
 * @Version 1.0
 **/

@Component
public class DataCollection {

    private static final Logger logger = LoggerFactory.getLogger(DataCollection.class);

    @Autowired
    private DefaultMQProducer producer;

    /**
     * 采集文件夹下所有的文件，包括文件夹
     * @param path
     */
    public void collect(String path) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        File file = new File(path);

        // 文件不存在则返回
        if(!file.exists()) {
            logger.error("不存在该文件路径: {}", path);
            return;
        }

        // 文件压缩
//        String zipPath = path.substring(0, path.lastIndexOf(File.separator)) + path.substring(path.lastIndexOf(File.separator) + 1, path.lastIndexOf("."));
//        try {
//            CompressUtils.writeByApacheZipOutputStream(path, zipPath, "采集程序自动压缩，读取后会自动删除");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 读取文件为字节数组
        byte[] message = FileUtils.File2byte(path);

        // 消息切割
        int orderID = 0;
        int maxSize = 4096;
        int hashCode = message.hashCode();

        // 切割消息，rocket能够接收的消息大小为4096
        while (message.length > maxSize) {
            byte[] subBytes = ByteUtils.subBytes(message, 0, maxSize);
            // 调用消息队列进行传输
            Message sendMessage = new Message("DemoTopic", "FileSynchronization", subBytes);
            message = ByteUtils.subBytes(message, maxSize, message.length - maxSize);
            producer.send(sendMessage, (mqs, msg, arg) -> {
                int index = hashCode % mqs.size();
                return mqs.get(index);
            }, orderID);
            logger.warn("发送的消息id为: {}", orderID);
            orderID += 1;
        }

        // 传输最后一段消息
        if(message.length > 0) {
            // 调用消息队列进行传输
            Message sendMessage = new Message("DemoTopic", "FileSynchronization", message);
            producer.send(sendMessage, (mqs, msg, arg) -> {
                int index = hashCode % mqs.size();
                return mqs.get(index);
            }, orderID);
            logger.warn("发送的消息id为: {}", orderID);

        }

        // 发送文件结束同步命令
        String end = "EOF";
        producer.send(new Message("DemoTopic", "FileSynchronization", end.getBytes()), (mqs, msg, arg) -> {
            int index = hashCode % mqs.size();
            return mqs.get(index);
        }, orderID);

        logger.warn("消息传输完毕");

    }
}
