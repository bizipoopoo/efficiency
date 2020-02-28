package com.ezsyncxz.efficiency;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.data.DataCollection;
import com.ezsyncxz.efficiency.redis.RedisUtil;
import com.ezsyncxz.efficiency.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    public void testMq() {
        String message = "demo msg test";
        logger.info("开始发送消息：" + message);
        Message sendMsg = new Message("DemoTopic", "DemoTag", message.getBytes());
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
        logger.info("消息发送响应信息：" + sendResult.toString());
    }

    @Test
    public void testOrderlyMsg() {
        String path = "D:\\chenwj\\dev\\test\\efficiency_src\\mysql-connector-java-5.1.7-bin.zip";
        try {
            dataCollection.collect(path);
        } catch (IOException e) {
            e.printStackTrace();
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

    @Test
    public void testZsGet() {
        redisUtil.zsSetAndSorte("demo", "b", 2.0);
        redisUtil.zsSetAndSorte("demo", "a", 2.0);
        redisUtil.zsSetAndSorte("demo", "c", 3.0);
        Set<Object> demo = redisUtil.zsGetDesc("demo");
        Iterator<Object> iterator = demo.iterator();
        while (iterator.hasNext()) {
            System.out.println((String) iterator.next());
        }
    }

    @Test
    public void testTransfer() {
        String filePath = "D:\\chenwj\\dev\\test\\efficiency_tar\\ideaIU-2019.3.1.exe";
        String url = "http://127.0.0.1:8080/receiveServlet/name=ideaIU-2019.3.1.exe&tarPath=" + filePath;
        String srcPath = "D:\\chenwj\\dev\\test\\efficiency_src\\ideaIU-2019.3.1.exe";


        //文件转发
        byte[] buffer = FileUtils.File2byte(srcPath);

        StringBuilder sb2 = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        //String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";

        URL uri = null;
        try {
            uri = new URL(url);
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) uri.openConnection();
                conn.setReadTimeout(60 * 1000); // 缓存的最长时间
                conn.setDoInput(true);// 允许输入
                conn.setDoOutput(true);// 允许输出
                conn.setUseCaches(false); // 不允许使用缓存
                conn.setRequestMethod("POST");
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Charsert", "UTF-8");
                conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

                DataOutputStream outStream;
                outStream = new DataOutputStream(conn.getOutputStream());
                InputStream in = null;

                // 发送文件数据
                outStream.write(buffer);
                // 得到响应码
                int res = conn.getResponseCode();
                if (res == 200) {
                    in = conn.getInputStream();
                    int ch;
                    sb2 = new StringBuilder();
                    while ((ch = in.read()) != -1) {
                        sb2.append((char) ch);
                    }
                }
                outStream.close();
                conn.disconnect();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
