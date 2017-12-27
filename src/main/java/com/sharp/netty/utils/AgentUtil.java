package com.sharp.netty.utils;

import com.sharp.netty.common.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author QinMingrui
 * 
 *         访问agent接口工具
 *
 */
public class AgentUtil {
	private static final String AGENT_SERVER_URL = "http://localhost:18080/SharpCloudAgent/agent";

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
	 * @param mac
	 * @return
	 */
	public static DeviceInfo getDeviceInfoByMac(String mac) {
		DeviceInfo deviceInfo = null;
		String url = AGENT_SERVER_URL + "/mac/device?mac=" + mac + "&appSecret=" + Util.APP_SECRET;
		String result = HttpUtils.sendHttpGetRequest(url);
		if (StringUtil.isNotBlank(result)) {
			deviceInfo = (DeviceInfo) JsonUtil.getBeanFromJson(result, DeviceInfo.class);
		}
		return deviceInfo;
	}

	/**
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
	 * @param "deviceInfo
	 * @return
	 */
	public static String updateDeviceInfo(DeviceInfo deviceInfoInput) {
		String updateDeviceInfoFlg = "";
		String url = AGENT_SERVER_URL + "/device?appSecret=" + Util.APP_SECRET;
		String inputJsonString = JsonUtil.getJsonFromBean(deviceInfoInput);
		updateDeviceInfoFlg = HttpUtils.sendHttpPutRequest(url, inputJsonString);
		return updateDeviceInfoFlg;
	}
}
