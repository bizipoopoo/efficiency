package com.ezsyncxz.efficiency.gap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName Gap
 * @Description 模拟网闸读写行为
 * @Author chenwj
 * @Date 2020/2/24 13:43
 * @Version 1.0
 **/

public class Gap {

    private static final Logger logger = LoggerFactory.getLogger(Gap.class);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);


    public void read() {
        lock.readLock().lock();
        logger.warn("{}正在读取数据...", Thread.currentThread().getName());
        // 读数据
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        logger.warn("{}读取数据完毕！", Thread.currentThread().getName());
        lock.readLock().unlock();
    }

    public void write() {
        lock.writeLock().lock();
        logger.warn("{}准备写入数据...", Thread.currentThread().getName());
        // 写数据
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        logger.warn("{}写入数据完毕！", Thread.currentThread().getName());
        lock.writeLock().unlock();
    }

    public static void main(String[] args) {

        Gap gap = new Gap();

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        gap.read();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        gap.write();
                    }
                }
            }).start();
        }

    }
}
