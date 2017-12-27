package com.sharp.netty.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author QinMingrui
 * 
 * 获取wifiupdate对应的配置工具类
 *
 */
public class WifiConfig {
	private final static Log log = LogFactory.getLog(WifiConfig.class);

	public static String getValue(String key) {

		String result = "";
		
		// 获取最新的协议版本
		Resource resource = new FileSystemResource("/wifiupdate.properties");
		try {
			Properties wifiProperties = PropertiesLoaderUtils.loadProperties(resource);
			result = wifiProperties.getProperty(key);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return result;
	}
}
