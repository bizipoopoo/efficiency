package com.ezsyncxz.efficiency.fileMonitor;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.ezsyncxz.efficiency.data.DataCollection;
import com.ezsyncxz.efficiency.utils.ApplicationContextUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

public class FileListener implements FileAlterationListener {

    private static final Logger logger = LoggerFactory.getLogger(FileListener.class);

    FileMonitor monitor = null;

    @Override
    public void onStart(FileAlterationObserver observer) {
//        logger.warn("正在监控文件 文件夹:{}", observer.getDirectory().getAbsolutePath());
    }

    @Override
    public void onDirectoryCreate(File directory) {
        logger.warn("监控到文件夹创建动作，开始同步数据 文件夹:{}", directory.getName());
    }

    @Override
    public void onDirectoryChange(File directory) {
        logger.warn("监听到文件夹变化动作，开始增量同步 文件夹:{}", directory.getName());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        logger.warn("监听到文件夹删除动作 文件夹：{}", directory.getName());
    }

    @Override
    public void onFileCreate(File file) {
        logger.warn("监听到文件新建动作，启动同步任务，开始文件同步 文件名:{}", file.getName());
        try {
            DataCollection dataCollection = ApplicationContextUtils.getBean(DataCollection.class);
            dataCollection.collect(file.getAbsolutePath());
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

    @Override
    public void onFileChange(File file) {
        logger.warn("监听到文件变化动作，开始增量同步 文件名:{}", file.getName());
    }

    @Override
    public void onFileDelete(File file) {
        logger.warn("监听到文件删除动作 文件名:{}", file.getName());
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
//        logger.warn("关闭文件监控");
    }

}