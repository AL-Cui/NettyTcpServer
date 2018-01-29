package com.sharp.netty.utils;

import com.sharp.netty.common.*;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Duo.Cui
 *
 * 访问agent接口工具
 */
public class AgentUtil {
    //	private static final String AGENT_SERVER_URL = "http://localhost:18080/SharpCloudAgent/agent";
    private static final String AGENT_SERVER_URL = "http://10.191.200.13:18080/SharpCloudAgent";

    /**
     * @param mac
     * @return
     */
    public static String checkBindingStatusByMac(String mac) {
        String ret = "";
        String url = AGENT_SERVER_URL + "/checkBindingStatusByMac?";
        url += HttpUtils.addUrlParamPair("mac", mac);
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        ret = HttpUtils.sendHttpGetRequest(url);

        return ret;
    }

    /**
     * @param mac
     * @return
     */
    public static String smartSet(String mac) {
        String ret = "";
        String url = AGENT_SERVER_URL + "/smartSet?";
        url += HttpUtils.addUrlParamPair("mac", mac);
        url += HttpUtils.addUrlParamPair("openId", "");
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        ret = HttpUtils.sendHttpGetRequest(url);

        return ret;
    }

    /**
     * @param mac
     * @return
     */
    public static String getCityNameByIp(String mac, String ip) {
        String ret = "";
        String url = AGENT_SERVER_URL + "/getCityNameByIp?";
        url += HttpUtils.addUrlParamPair("macAddress", mac);
        url += HttpUtils.addUrlParamPair("ip", ip);
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        ret = HttpUtils.sendHttpGetRequest(url);

        return ret;
    }

    /**
     * @param mac
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<DeviceBindInfo> getDeviceBindInfoByMac(String mac) {
        List<DeviceBindInfo> deviceBindInfoList = new ArrayList<DeviceBindInfo>();

        String url = AGENT_SERVER_URL + "/deviceBind/list/mac?";
        url += HttpUtils.addUrlParamPair("mac", mac);
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        List<Object> listObjcet = JsonUtil.getJsonListFromString(HttpUtils.sendHttpGetRequest(url),
                DeviceBindInfo.class);
        for (Object objcet : listObjcet) {
            deviceBindInfoList.add((DeviceBindInfo) objcet);
        }
        return deviceBindInfoList;
    }

    /**
     * @param mac
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<DeviceBindInfo> getDeviceBindInfoListByMac(String mac) {
        List<DeviceBindInfo> deviceBindInfoList = new ArrayList<DeviceBindInfo>();

        String url = AGENT_SERVER_URL + "/deviceBind/mac/list?";
        url += HttpUtils.addUrlParamPair("mac", mac);
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        List<Object> listObjcet = JsonUtil.getJsonListFromString(HttpUtils.sendHttpGetRequest(url),
                DeviceBindInfo.class);
        for (Object objcet : listObjcet) {
            deviceBindInfoList.add((DeviceBindInfo) objcet);
        }
        return deviceBindInfoList;
    }

    /**
     * 解绑所有用户
     * @param mac
     * @return
     */
    public static String unbindAllUser(String mac) {
        String result = "";

        String url = AGENT_SERVER_URL + "/user/unbindAllUser?";
        url += HttpUtils.addUrlParamPair("mac", mac);
        url += HttpUtils.addUrlParamPair("appSecret", Util.APP_SECRET);

        result = HttpUtils.sendHttpPostRequest(url, "{}");

        return result;
    }

    /**
     * 通过Mac地址获取设备信息
     * @param mac
     * @return
     */
    public static DeviceInfo getDeviceInfoByMac(String mac) {
        DeviceInfo deviceInfo = null;
        String url = AGENT_SERVER_URL + "/mac/getDeviceInfo" + "?appSecret=" + Util.APP_SECRET;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("mac", mac);
        String bodyString = JsonUtil.getJsonFromMapObject(jsonMap);
        String result = OkHttpClientUtils.sendHttpPostRequest(url, bodyString);
        if (StringUtil.isNotBlank(result)) {
            deviceInfo = (DeviceInfo) JsonUtil.getBeanFromJson(result, DeviceInfo.class);
        }
        return deviceInfo;
    }

    /**
     * 通过OpenId获取设备的绑定信息
     * @param openId
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<DeviceBindInfo> getDeviceBindInfoByOpenId(String openId) {
        List<DeviceBindInfo> deviceBindInfoList = new ArrayList<DeviceBindInfo>();
        String url = AGENT_SERVER_URL + "/deviceBind/list/openId?openId=" + openId + "&appSecret="
                + Util.APP_SECRET;
        String result = HttpUtils.sendHttpGetRequest(url);
        if (StringUtil.isNotBlank(result)) {
            List<Object> listObjcet = JsonUtil.getJsonListFromString(result, DeviceBindInfo.class);
            for (Object objcet : listObjcet) {
                deviceBindInfoList.add((DeviceBindInfo) objcet);
            }
        }
        return deviceBindInfoList;
    }

    /**
     * 通过deviceId获取设备信息
     * @param deviceId
     * @return
     */
    public static DeviceInfo getDeviceInfo(String deviceId) {
        DeviceInfo deviceInfo = null;
        String url = AGENT_SERVER_URL + "/device?deviceId=" + deviceId + "&appSecret=" + Util.APP_SECRET;
        String result = HttpUtils.sendHttpGetRequest(url);
        if (StringUtil.isNotBlank(result)) {
            deviceInfo = (DeviceInfo) JsonUtil.getBeanFromJson(result, DeviceInfo.class);
        }
        return deviceInfo;
    }

    /**
     * 更新Agent那边存储的设备信息（主要是版本信息）
     * unchecked
     * @param "deviceInfo
     * @return
     */
    public static String updateDeviceInfo(DeviceInfo deviceInfoInput,String kind) {
        String updateDeviceInfoFlg;
        String url = AGENT_SERVER_URL + "/mac/updateDeviceVersion?appSecret=" + Util.APP_SECRET;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("mac", deviceInfoInput.getMacAddress());
        jsonMap.put("kind", kind);
        jsonMap.put("newVersion", deviceInfoInput.getWifiVersion());

        String bodyString = JsonUtil.getJsonFromMapObject(jsonMap);

        updateDeviceInfoFlg = OkHttpClientUtils.sendHttpPostRequest(url, bodyString);

        return updateDeviceInfoFlg;
    }

    /***
     * 发送版本更新的通知给Agent
     * unchecked
     * @param mac
     * @param newWifiVersion
     * @param wifi
     */
    public static void sendUpdateMessage(String mac, String newWifiVersion, String wifi) {
        String userOpenId = AgentUtil.getDeviceBindMasterByMac(mac);
        if (userOpenId  != null){
            String url = AGENT_SERVER_URL + "/mac/sendUpdateMessage?appSecret=" + Util.APP_SECRET;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("mac", mac);
            jsonMap.put("userOpenId", userOpenId);
            jsonMap.put("kind", wifi);
            jsonMap.put("newVersion",newWifiVersion);

            String bodyString = JsonUtil.getJsonFromMapObject(jsonMap);
            OkHttpClientUtils.sendHttpPostRequest(url,bodyString);
        }


    }

    /***
     * 通过Mac地址获取设备的主用户信息
     * @param mac
     * @return userOpenId
     */
    private static String getDeviceBindMasterByMac(String mac) {
        String url = AGENT_SERVER_URL + "/mac/getDeviceMaster?appSecret=" + Util.APP_SECRET;
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("mac", mac);
        String bodyString = JsonUtil.getJsonFromMapObject(jsonMap);
        String result = OkHttpClientUtils.sendHttpPostRequest(url, bodyString);
        DeviceBindMaster resultObject = (DeviceBindMaster) JsonUtil.getBeanFromJson(result,DeviceBindMaster.class);
        String userOpenId = resultObject.getData();
        return userOpenId;

    }
}
