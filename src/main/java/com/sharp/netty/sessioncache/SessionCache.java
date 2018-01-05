package com.sharp.netty.sessioncache;

import com.sharp.netty.common.KVStoreConfig;
import com.sharp.netty.common.KVStoreUtils;
import com.sharp.netty.common.StringUtil;
import com.sharp.netty.utils.Util;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QinMingrui
 * <p>
 * Session操作类
 */
public class SessionCache {
    private final static Log log = LogFactory.getLog(SessionCache.class);
    private static SessionCache session = null;
    /**
     * 设备连接信息[sessions]
     */
    private Map<String, SocketChannel> sessions = new HashMap<>();

    private static final String value = KVStoreConfig.getIpAddress();

    /**
     * 获取唯一实例
     */
    public static SessionCache getInstance() {
        log.debug(" <<< Session单例获取 <<< ");
        if (session == null) {
            session = new SessionCache();
        }
        return session;
    }

    /**
     * 获取设备连接信息[sessions]
     *
     * @return 设备连接信息[sessions]
     */
    public Map<String, SocketChannel> getSessions() {
        return sessions;
    }

    /**
     * 设置设备连接信息[sessions]
     *
     * @param "设备连接信息 [sessions]
     */
    public void setSessions(Map<String, SocketChannel> sessions) {
        this.sessions = sessions;
    }

    /**
     * 增加设备连接信息[sessions]
     *
     * @param "MAC地址作为键"      [mac]
     * @param "IoSession对象作为值 [session]
     */
    public void save(String mac, SocketChannel channel) {
        sessions.put(mac, channel);
        log.info("Map里现存的session数量=" + sessions.size());
        String clientIp = channel.remoteAddress().toString();
        KVStoreUtils.putHashmapField(mac, value);

        String kvIp = KVStoreUtils.getHashmapField(Util.KV_PORT_KEY, mac);
        log.info("Redis里目前存的IP="+kvIp);
        KVStoreUtils.putHashmapField(Util.KV_PORT_KEY, mac, clientIp);   //此处只存储唯一的ipport

//		String kvIp = KVStoreUtils.getHashmapField(Util.KV_PORT_KEY, mac);
//		if (StringUtil.isNotBlank(kvIp)) {
//			if (!kvIp.contains(clientIp)) {
//				String ipvalue = kvIp + ";" + clientIp;
//				KVStoreUtils.putHashmapField(Util.KV_PORT_KEY, mac, ipvalue);
//			}
//		} else {
//			KVStoreUtils.putHashmapField(Util.KV_PORT_KEY, mac, clientIp);
//		}
    }

    /**
     * 查找设备连接信息[sessions]
     *
     * @param "MAC地址作为键" [key]
     * @return 连接信息
     */
    public SocketChannel isExists(String key) {
        if (sessions.containsKey(key)) {
            return sessions.get(key);
        }
        return null;
    }

    /**
     * 删除设备连接信息[sessions]
     *
     * @param "MAC地址作为键" [mac]
     */
    public void remove(String mac, String clientIp) {
        if (sessions.containsKey(mac)) {
            sessions.remove(mac);
        }
        log.info("Map里现存的session数量=" + sessions.size());
        String ipValue = KVStoreUtils.getHashmapField(Util.KV_PORT_KEY, mac);
        if (ipValue != null){
            KVStoreUtils.removeHahmapField(mac);
            KVStoreUtils.removeHahmapField(Util.KV_PORT_KEY, mac);
        }
//        if (clientIp.equals(ipvalue)) {
//            KVStoreUtils.removeHahmapField(mac);
//            KVStoreUtils.removeHahmapField(Util.KV_PORT_KEY, mac);
//        } else {
//            String ipvalueTemp = ipvalue.replaceAll(clientIp + ";", "");
//            KVStoreUtils.putHashmapField(Util.KV_PORT_KEY, mac, ipvalueTemp);
//        }
    }
}