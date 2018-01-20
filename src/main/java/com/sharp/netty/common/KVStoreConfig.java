package com.sharp.netty.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 读取配置
 *
 * @author Duo.Cui
 */
public class KVStoreConfig {

    private static Properties kvsProps;
    private static final Logger logger = LoggerFactory.getLogger(KVStoreConfig.class);

    /**
     * 设入KVStore配置
     *
     * @param prop 配置信息
     */
    public static void setKvsProps(Properties prop) {
        KVStoreConfig.kvsProps = prop;
    }

    /**
     * 获得Properties值
     *
     * @param key Properties的key
     * @return Properties的值
     */
    public static String getValue(String key) {
        return kvsProps.getProperty(key);
    }

    /**
     * 获得IP地址
     *
     * @return 获得IP地址
     */
    public static String getIpAddress() {
        String ret = "";
        String localIp = "";
        String outerIp = "";

        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        outerIp = ip.getHostAddress();
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localIp = ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        if (StringUtil.isNotBlank(outerIp)) {
            ret = outerIp;
        } else {
            ret = localIp;
        }

        return ret;
    }
}
