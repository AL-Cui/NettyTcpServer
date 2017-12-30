package com.sharp.netty.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author QinMingrui
 * <p>
 * 获取wifiupdate对应的配置工具类
 */
public class WifiConfig {
    private final static Logger log = LoggerFactory.getLogger(WifiConfig.class);

    public static String getValue(String key) {

        String result = "";

        // 获取最新的协议版本
        InputStream inputStream = WifiConfig.class.getResourceAsStream("/wifiupdate.properties");
        Properties wifiProperties = new Properties();
        try {
            wifiProperties.load(inputStream);
            result = wifiProperties.getProperty(key);
        } catch (IOException e) {
            log.error("读取版本资源文件" + e.getMessage());
        }
        return result;
    }
}
