package com.ezsyncxz.efficiency.data;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.entity.FileFragment;
import com.ezsyncxz.efficiency.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

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

    public static final String tar = "D:\\chenwj\\dev\\test\\efficiency_tar\\";

    /**
     * 采集文件夹下所有的文件，包括文件夹
     *
     * @param src
     */
    public void collect(String src) throws IOException, InterruptedException, RemotingException, MQClientException, MQBrokerException {

        File file = new File(src);

        String filename = file.getName();

        // 文件不存在则返回
        if (!file.exists()) {
            logger.error("不存在该文件路径: {}", src);
            return;
        }

//        boolean needCompress = false;
//        // 如果是文件夹，则进行文件压缩
//        if(file.isDirectory()) {
//            String zipPath = src.substring(0, src.lastIndexOf(File.separator)) + src.substring(src.lastIndexOf(File.separator) + 1, src.lastIndexOf("."));
//            try {
//                CompressUtils.writeByApacheZipOutputStream(src, zipPath, "采集程序自动压缩，读取后会自动删除");
//                src = zipPath;
//                needCompress = true;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//         //读取文件为字节数组
//        byte[] message = FileUtils.File2byte(src);
//        int msgTotalSize = message.length;
//        int orderID = 0;
//        int maxSize = 2048;
//        String tag = MD5Utils.MD54bytes(message);
//        int msgCount = (int) Math.ceil((message.length / 2048.0));
//
//        // 切割消息，rocket能够接收的消息大小为4096
//        while (message.length > maxSize) {
//            byte[] subBytes = ByteUtils.subBytes(message, 0, maxSize);
//            // 调用消息队列进行传输
//            FileFragment fileFragment = FileFragment.newBuilder()
//                    .body(subBytes)
//                    .msgCount(msgCount)
//                    .needCompress(needCompress)
//                    .tarPath(tar)
//                    .filename(filename)
//                    .score(orderID)
//                    .build();
//            Message sendMessage = new Message("DemoTopic", tag, JSONObject.toJSONString(fileFragment).getBytes());
//            message = ByteUtils.subBytes(message, maxSize, message.length - maxSize);
//            producer.send(sendMessage, (mqs, msg, arg) -> {
//                int o = (int) arg;
//                int index = o % mqs.size();
//                return mqs.get(index);
//            }, orderID);
////            logger.warn("发送的消息id为: {}", orderID);
//            orderID += 1;
//        }
//
//        // 传输最后一段消息
//        if(message.length > 0) {
//            // 调用消息队列进行传输
//            FileFragment fileFragment = FileFragment.newBuilder()
//                    .needCompress(needCompress)
//                    .body(message)
//                    .msgCount(msgCount)
//                    .tarPath(tar)
//                    .filename(filename)
//                    .score(orderID)
//                    .build();
//            Message sendMessage = new Message("DemoTopic", tag, JSONObject.toJSONString(fileFragment).getBytes());
//            producer.send(sendMessage, (mqs, msg, arg) -> {
//                int o = (int) arg;
//                int index = o % mqs.size();
//                return mqs.get(index);
//            }, orderID);
////            logger.warn("发送的消息id为:{}", orderID);
//        }
//
//        // 如果是文件夹，可以删除压缩后的文件
//        if(needCompress) {
//
//        }
//
//        logger.warn("消息传输完毕 消息总大小:{}字节 消息总数:{} 消息哈希：{} 消息目标路径: {}", msgTotalSize, msgCount, tag, tar + filename);

        // 用文件随机读写的方式读取文件片段
        int len = 3000000; // 每个消息文件片段的大小
        int off = 0; // 每个消息片段的偏移量
        byte[] bytes = new byte[len]; // 缓冲接收文件
        long length = file.length(); // 文件大小
        RandomAccessFile r = new RandomAccessFile(src, "r");
        int rLen = 0; // 每次读取的字节数
        String tag = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        while ((rLen = r.read(bytes)) > 0) {

            if(rLen != bytes.length) {
                bytes = ByteUtils.subBytes(bytes, 0, rLen);
                logger.warn("最后一个文件不足{}B", len);
            }

            FileFragment fileFragment = FileFragment.newBuilder()
                    .filename(filename)
                    .tarPath(tar)
                    .body(bytes)
                    .needCompress(false)
                    .length(length)
                    .off(off)
                    .startTime(startTime)
                    .build();

            Message sendMessage = new Message("DemoTopic", tag, JSONObject.toJSONString(fileFragment).getBytes());
            producer.send(sendMessage);
            off += rLen;
        }
        r.close();
    }

    public static void main(String[] args){

    }
}
