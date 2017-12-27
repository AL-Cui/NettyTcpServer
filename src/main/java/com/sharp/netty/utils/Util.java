package com.sharp.netty.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Util {
    public static final String[] strings ={"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
    public static final int[] indexInt = {1, 3, 5, 7, 9, 11, 13, 15};
    String[] numberInt = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    public static final Logger logger = LoggerFactory.getLogger(Util.class);
    /** NULL字符串 */
    public static final String VALUE_NULL = "null";

    /** 字符数字0 */
    public static final String VALUE_STRING_ZERO = "0";
    /** 字符数字1 */
    public static final String VALUE_STRING_ONE = "1";
    /** Xml:节点 */
    public static final String NODE_TCP_MSG = "tcp_msg";

    /** Xml:节点 */
    public static final String NODE_MSG = "msg";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_HEARTBEATRES = "heartbeatres";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_HEARTBEAT = "heartbeat";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_NOTIFY = "notify";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_LINKRESET = "linkreset";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_UPDATE = "updateversionres";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_MACHVER = "updatemachverres";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_RECEIVE = "receiveverionres";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_RECEIVEMACHVER = "receivemachverres";

    /** Xml:节点信息内容 */
    public static final String MSG_VALUE_CTRLNOTIFY = "ctrlnotify";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_UPDATEVERSION = "updateversion";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_UPDATEMACHVER = "updatemachver";

    /** Xml:节点 */
    public static final String NODE_CMD = "cmd";

    /** Xml:节点 */
    public static final String NODE_URL = "url";

    /** Xml:节点 */
    public static final String NODE_VERSION = "version_number";

    /** Xml:节点 */
    public static final String NODE_VERSIONMACHVER = "mach_version_number";

    /** Xml:节点 */
    public static final String NODE_UPDATEFLAG = "update_flag";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE = "initialsetcmd";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_CHECKCMD = "checkcmd";

    /** Xml:节点信息内容 */
    public static final String CMD_VALUE_BOXDELETE = "boxdelete";

    /** Xml:节点 */
    public static final String NODE_DATA = "data";

    /** Xml:节点 */
    public static final String NODE_MAC_ADDRESS = "mac_address";

    /** Xml:节点 */
    public static final String NODE_SERVER_TIME = "server_time";

    /** JKS密钥库 */
    public static final String KEY_JKS = "JKS";

    /** X.509密钥管理器 */
    public static final String KEY_MANAGER_X509 = "SunX509";

    /** SSL协议版本 */
    public static final String SSL_VERSION = "TLSV1.2";
    public static final String SHARP_VERSION = "SHARP_AP_1_0.1.43";
    public static final String MACHINE_VERSION = "SHARPPCIANRS_AP_FPCH70_3_0";


    /** system file encoding property */
    public static final String SERVER_ENCODING = "file.encoding";

    /** kvstore key */
    public static final String KV_PORT_KEY = "shcloudsharpcn1";

    public static final String APP_SECRET = "OPBKAEhw1gTa0RthYTGqwk1d5nwClollldU1qpvUBrR";
    public static String getRandomMacAddress(){
        String result = "";
        int  index = 0;
        Random random = new Random();
        for (int i = 1;i<=17;i++){
            if (i ==2){
                index = indexInt[random.nextInt(8)];
                result += strings[index];
            }else if (i%3 == 0){
                result += "-";
            }else {
                index = random.nextInt(16);
                result += strings[index];
            }
        }
        return result;
    }
}
