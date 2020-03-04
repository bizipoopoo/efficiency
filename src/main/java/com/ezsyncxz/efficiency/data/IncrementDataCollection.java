package com.ezsyncxz.efficiency.data;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @ClassName IncrementDataCollection
 * @Description 增量数据同步
 * @Author chenwj
 * @Date 2020/3/3 18:41
 * @Version 1.0
 **/

@Component
public class IncrementDataCollection {
    private static final Logger logger = LoggerFactory.getLogger(AllDataCollection.class);

    public static final String tar = "D:\\chenwj\\dev\\test\\efficiency_copy\\";

    /**
     * 采集文件夹下所有的文件，包括文件夹
     *
     * @param src
     */
    public void collect(String src) throws IOException, InterruptedException, RemotingException, MQClientException, MQBrokerException {

        File file = new File(src);

        // 文件不存在则返回
        if (!file.exists()) {
            logger.error("不存在该文件路径: {}", src);
            return;
        }

        // 发送消息给服务端，通知服务端发送校验和过来

        // 比对校验和

        // 发送增量数据

        RandomAccessFile accessFile = new RandomAccessFile(src, "r");
        int length = 0;
        double sumL = 0 ;
        byte[] sendBytes = null;
        Socket socket = null;
        DataOutputStream dos = null;
        boolean bool = false;
        try {
            long l = file.length();
            socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 48123));
            dos = new DataOutputStream(socket.getOutputStream());
            sendBytes = new byte[1024];

            //传输文件路径,前4个字节是长度
            String fileName = file.getName();
            String filePath = tar + File.separator + fileName;
            int len = filePath.getBytes().length;
            byte[] lenBytes = ByteUtils.intToByteArray(len);
            byte[] bytes = ByteUtils.concateBytes(lenBytes, filePath.getBytes());
            dos.write(bytes);
            dos.flush();

            // 传输文件内容
            while ((length = accessFile.read(sendBytes)) > 0) {
                sumL += length;
                logger.warn("已传输:{}", ((sumL/l)*100)+"%");
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
            //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
            if(sumL==l){
                bool = true;
            }
        }catch (Exception e) {
            System.out.println("客户端文件传输异常");
            bool = false;
            e.printStackTrace();
        } finally{
            if (dos != null)
                dos.close();
            if (socket != null)
                socket.close();
        }
        if(bool) {
            logger.warn("传输完毕！");
        } else {
            logger.warn("文件传输失败！");
        }
    }
}
