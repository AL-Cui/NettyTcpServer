package com.sharp.netty.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author QinMingrui
 * <p>
 * KVStore操作的工具类
 */
@SpringBootApplication
public class KVStoreUtils {

    private static final Logger log = LoggerFactory.getLogger(KVStoreUtils.class);

    private static JedisPool pool = null;

    //private static final String key = KVStoreConfig.getValue("kvs_app_key");
    private static final String key = "water";
    //private static final String host = KVStoreConfig.getValue("kvs_host");
//	private static final String host = "6a3c057beb624b40.m.cnbja.kvstore.aliyuncs.com";
//    private static final String host = "127.0.0.1";
    private static final String host = "localhost";
    //private static final int port = Integer.valueOf(KVStoreConfig.getValue("kvs_port"));
    private static final int port = 6379;//密码123
    private static final String instanceId = "NmEzYzA1N2JlYjYyNGI0MA==";
    private static final String password = "MTM4N0htUzAyMDI=";

    static {
        initialize();
    }

    public static void initialize() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            // 最大空闲连接数, 应用自己评估，不要超过KVStore每个实例最大的连接数
            config.setMaxIdle(30);
            // 最大连接数, 应用自己评估，不要超过KVStore每个实例最大的连接数
            config.setMaxTotal(100);
            config.setTestOnBorrow(false);
            config.setTestOnReturn(false);
            // 鉴权信息由用户名:密码拼接而成
//            String authStr = MessageFormat.format("{0}:{1}", EncryptProperty(instanceId), EncryptProperty(password));
            String authStr = "123";
            pool = new JedisPool(config, host, port, 3000, authStr);

        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public static void putHashmapField(String field, String value) {
        try {
            Jedis jedis = pool.getResource();
            jedis.hset(key, field, value);
            log.info(MessageFormat.format("set key[{0}],field[{1}],value[{2}] to KVS", key, field, value));
            jedis.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

//    public static String getHashmapField(String field) {
//
//        String ret = "";
//        try {
//            Jedis jedis = pool.getResource();
//            ret = jedis.hget(key, field);
//            jedis.close();
//            log.info(MessageFormat.format("get key[{0}],field[{1}] from KVS, value is [{2}]", key, field, ret));
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//        return ret;
//
//    }

    public static void putHashmapField(String kvKety, String field, String value) {
        try {
            Jedis jedis = pool.getResource();
            jedis.hset(kvKety, field, value);
            log.info(MessageFormat.format("set key[{0}],field[{1}],value[{2}] to KVS", kvKety, field, value));
            jedis.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

//    public static void putHashmapField(String kvKety, Map<String, String> hash) {
//        try {
//            Jedis jedis = pool.getResource();
//            jedis.hmset(kvKety, hash);
//            log.info(MessageFormat.format("set key[{0}]] to KVS", kvKety));
//            jedis.close();
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//    }

    public static String getHashmapField(String kvKety, String field) {

        String ret = "";
        try {
            Jedis jedis = pool.getResource();
            ret = jedis.hget(kvKety, field);
            jedis.close();
            log.info(MessageFormat.format("get key[{0}],field[{1}] from KVS, value is [{2}]", kvKety, field, ret));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return ret;

    }

    public static Map<String, String> getAllField(String kvKety) {

        Map<String, String> retMap = new HashMap<String, String>();
        try {
            Jedis jedis = pool.getResource();
            retMap = jedis.hgetAll(kvKety);
            jedis.close();
            log.info(MessageFormat.format("get key[{0}]from KVS, value is [{1}]", kvKety, retMap.size()));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return retMap;

    }

    public static Long removeHahmapField(String kvKety, String field) {
        Long ret = null;
        try {
            Jedis jedis = pool.getResource();
            ret = jedis.hdel(kvKety, field);
            jedis.close();
            log.info(MessageFormat.format("remove key[{0}],field[{1}] from KVS ", kvKety, field));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return ret;
    }

    public static Long removeHahmapField(String field) {
        Long ret = null;
        try {
            Jedis jedis = pool.getResource();
            ret = jedis.hdel(key, field);
            jedis.close();
            log.info(MessageFormat.format("remove key[{0}],field[{1}] from KVS ", key, field));
        } catch (Exception e) {
            log.error(e.toString());
        }
        return ret;
    }

//    public static Long clearKey(String kvKety) {
//        Long ret = null;
//        try {
//            Jedis jedis = pool.getResource();
//            ret = jedis.del(kvKety);
//            jedis.close();
//            log.info(MessageFormat.format("remove key [{0}] from KVS ", kvKety));
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//        return ret;
//    }
//
//    public static Long clearKey() {
//        Long ret = null;
//        try {
//            Jedis jedis = pool.getResource();
//            ret = jedis.del(key);
//            jedis.close();
//            log.info(MessageFormat.format("remove key [{0}] from KVS ", key));
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//        return ret;
//    }

//    private static String EncryptProperty(String propertyValue) {
//
//        byte[] asBytes = Base64.getDecoder().decode(propertyValue);
//        String decryptValue = "";
//        try {
//            decryptValue = new String(asBytes, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            log.error(e.toString());
//        }
//        return decryptValue;
//    }

}
