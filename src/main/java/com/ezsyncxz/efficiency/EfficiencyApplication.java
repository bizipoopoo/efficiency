package com.ezsyncxz.efficiency;

import com.ezsyncxz.efficiency.fileMonitor.FileListener;
import com.ezsyncxz.efficiency.fileMonitor.FileMonitor;
import com.ezsyncxz.efficiency.utils.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EfficiencyApplication {

    private static final Logger logger = LoggerFactory.getLogger(EfficiencyApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(EfficiencyApplication.class, args);
        ApplicationContextUtils.setApplicationContext(ctx);
        String monitorDirectory = "D:\\chenwj\\dev\\test\\efficiency_src";
        logger.warn("启动文件夹监控，正在监听{}", monitorDirectory);
        try {
            FileMonitor m = new FileMonitor(5000);
            m.monitor(monitorDirectory, new FileListener());
            m.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}