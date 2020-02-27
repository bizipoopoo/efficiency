package com.ezsyncxz.efficiency.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.ezsyncxz.efficiency.entity.FileFragment;
import com.ezsyncxz.efficiency.mq.annotation.MQConsumeService;
import com.ezsyncxz.efficiency.mq.entity.MQConsumeResult;
import com.ezsyncxz.efficiency.mq.processor.AbstractMQMsgProcessor;
import com.ezsyncxz.efficiency.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected MQConsumeResult consumeMessage(String tag, List<String> keys, MessageExt messageExt) {
//        logger.warn("{}接收到来自{}的消息，开始处理...", Thread.currentThread().getName(), tag);
//        try {
//            byte[] body = messageExt.getBody();
//            String bodyString = new String(body);
//            FileFragment fileFragment = JSONObject.parseObject(bodyString, FileFragment.class);
////            logger.warn("接收到消息 消息编号:{}", fileFragment.getScore());
//            redisUtil.zsSetAndSorte(tag, bodyString, fileFragment.getScore());
////            logger.warn("消息已写入缓存!");
//
//            // 当文件的所有片段读完，开始写入磁盘
////            logger.warn("当前消息总数为:{}, 传过来的消息总数为:{}", size, fileFragment.getMsgCount());
//            byte[] fileBody = new byte[0];
//            long size = redisUtil.zsGetSize(tag);
//            if (size == fileFragment.getMsgCount()) {
//                Set<Object> objects = redisUtil.zsGetAsc(tag);
//                for (Object object : objects) {
//                    String fragmentString = (String) object;
//                    FileFragment fragment = JSONObject.parseObject(fragmentString, FileFragment.class);
//                    fileBody = ByteUtils.concateBytes(fileBody, fragment.getBody());
//                }
//                FileUtils.byte2File(fileBody, fileFragment.getTarPath(), fileFragment.getFilename());
//                redisUtil.del(tag);
//                logger.warn("文件写入完毕!已删除缓存 文件路径:{}", fileFragment.getTarPath() + fileFragment.getFilename());
//            }
//            return MQConsumeResult.newBuilder().isSuccess(true).build();
//        }catch (Exception e) {
//            logger.warn("文件写入异常，删除缓存");
//            e.printStackTrace();
//            redisUtil.del(tag);
//        }

        // 用RandomAccessFile直接将拿到的片段写入磁盘
        byte[] body = messageExt.getBody();
        String bodyString = new String(body);
        FileFragment fileFragment = JSONObject.parseObject(bodyString, FileFragment.class);
        String tarPath = fileFragment.getTarPath();
        String filename = fileFragment.getFilename();
        String path = tarPath + File.separator + filename;
        File file = new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            RandomAccessFile w = new RandomAccessFile(path, "w");
            w.write(fileFragment.getBody(), fileFragment.getOff(), fileFragment.getBody().length);
            logger.warn("写入文件片段 文件名:{} 偏移量：{}", filename, fileFragment.getOff());
            w.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file.length() == fileFragment.getLength()) {
            logger.warn("文件写入完毕 文件:{}", tarPath + File.separator + filename);
        }
        return MQConsumeResult.newBuilder().isSuccess(true).build();
    }
}
