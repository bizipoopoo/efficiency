package com.ezsyncxz.efficiency.data;

import com.ezsyncxz.efficiency.utils.CompressUtils;
import com.ezsyncxz.efficiency.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName DataCollection
 * @Description 采集指定文件夹数据，将数据读取，归档，分块写入消息队列中，rocketMq能够存入的消息大小最大为4MB，因此我们需要对过大的数据进行切割
 * @Author chenwj
 * @Date 2020/2/24 15:46
 * @Version 1.0
 **/

public class DataCollection {

    private static final Logger logger = LoggerFactory.getLogger(DataCollection.class);

    /**
     * 采集文件夹下所有的文件，包括文件夹
     * @param path
     */
    public static void collect(String path) {

        File file = new File(path);

        // 文件不存在则返回
        if(!file.exists()) {
            logger.error("不存在该文件路径: {}", path);
            return;
        }

        // 文件压缩
        String zipPath = path.substring(0, path.lastIndexOf(File.separator) + 1) + File.separator + path.substring(path.lastIndexOf(File.separator)).split(".")[0];;
        try {
            CompressUtils.writeByApacheZipOutputStream(path, zipPath, "采集程序自动压缩，读取后会自动删除");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 读取文件为字节数组
        byte[] message = FileUtils.File2byte(zipPath);

        // 消息切割
        if(message.length > 4098) {

        }

        // 调用消息队列进行传输
    }
}
