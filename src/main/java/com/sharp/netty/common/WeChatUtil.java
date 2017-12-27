package com.sharp.netty.common;

import com.sharp.netty.utils.AgentUtil;
import com.sharp.netty.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author QinMingrui
 * 
 *         访问wechat接口工具类
 *
 */
public class WeChatUtil {
	private static final Logger log = LoggerFactory.getLogger(WeChatUtil.class);
	private static String wechat_url_status = "http://localhost:18080/SharpCloudWeb/wechatService/notifyConnStatus?appSecret="
			+ Util.APP_SECRET;
	private static String wechat_url_update = "http://localhost:18080/SharpCloudWeb/wechatService/notifyUpdateInfo?appSecret="
			+ Util.APP_SECRET;
	private static String wechat_url_notifyupdate = "http://localhost:18080/SharpCloudWeb/wechatService/updateNotify?appSecret="
			+ Util.APP_SECRET;

	/**
	 * 发送设备状态信息给微信
	 * 
	 * @param openIdList
	 * @param deviceId
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String sendConnStatusToWeChat(List<String> openIdList, String deviceId, String status) {
		log.info("【TCPSERVER】sendConnStatusToWeChat Start");
		Map<String, String> openIdMap = null;
		List<Map> tempList = new ArrayList<Map>();
		for (String openId : openIdList) {
			openIdMap = new HashMap<String, String>();
			openIdMap.put("openId", openId);
			tempList.add(openIdMap);
		}

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("openIdList", tempList);
		jsonMap.put("deviceId", deviceId);
		jsonMap.put("status", status);

		String jsonString = JsonUtil.getJsonFromMapObject(jsonMap);
		jsonMap = new HashMap<String, Object>();
		jsonMap = JsonUtil.getJsonFromString(HttpUtils.sendHttpPostRequest(wechat_url_status, jsonString));
		log.info("【TCPSERVER】sendConnStatusToWeChat End");
		return jsonMap.get("result").toString();
	}

	/**
	 * 发送固件更新结果给微信
	 * 
	 * @param "openId
	 * @param "deviceId
	 * @param updateFlg
	 * @return
	 */
	public static String sendUpdateInfoToWeChat(String mac, String updateFlg, String version,String updateType) {
		log.info("【TCPSERVER】sendUpdateInfoToWeChat Start");
		List<String> openIdList = new ArrayList<String>();
		String result = "";
		String deviceId = "";
		String masterOpenId = "";
		List<DeviceBindInfo> deviceBindInfoList = AgentUtil.getDeviceBindInfoListByMac(mac);

		if (deviceBindInfoList.size() != 0) {
			for (DeviceBindInfo deviceBindInfo : deviceBindInfoList) {
				// 获取用户信息
				if ("0".equals(deviceBindInfo.getAdvanceBindFlg())) {
					openIdList.add(deviceBindInfo.getOpenId());
					deviceId = deviceBindInfo.getDeviceId();
					if ("1".equals(deviceBindInfo.getFirstBindFlg())) {
						masterOpenId = deviceBindInfo.getOpenId();
					}
				}
			}
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("openIdList", openIdList);
			jsonMap.put("deviceId", deviceId);
			jsonMap.put("update_flag", updateFlg);
			jsonMap.put("version", version);
			jsonMap.put("updateType", updateType);
			jsonMap.put("masterOpenId", masterOpenId);

			String jsonString = JsonUtil.getJsonFromMapObject(jsonMap);
			jsonMap = new HashMap<String, Object>();
			jsonMap = JsonUtil.getJsonFromString(HttpUtils.sendHttpPostRequest(wechat_url_update, jsonString));
			result = jsonMap.get("result").toString();
		}
		log.info("【TCPSERVER】sendUpdateInfoToWeChat End");
		return result;
	}

	/**
	 * 发送固件更新结果给微信
	 * 
	 * @param "openId
	 * @param "deviceId
	 * @param "updateFlg
	 * @return
	 */
	public static String sendUpdateNotifyToWeChat(String mac, String newWifiVersion,String updateType) {
		log.info("【TCPSERVER】sendUpdateNotifyToWeChat Start");
		String result = "";
		String openId = "";
		String deviceId = "";

		List<DeviceBindInfo> deviceBindInfoList = AgentUtil.getDeviceBindInfoListByMac(mac);

		if (deviceBindInfoList.size() != 0) {
			for (DeviceBindInfo deviceBindInfo : deviceBindInfoList) {
				// 获取主用户信息
				if ("1".equals(deviceBindInfo.getFirstBindFlg())) {
					openId = deviceBindInfo.getOpenId();
					deviceId = deviceBindInfo.getDeviceId();
					break;
				}
			}
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("openId", openId);
			jsonMap.put("deviceId", deviceId);
			jsonMap.put("updateType", updateType);
			jsonMap.put("newWifiVersion", newWifiVersion);

			String jsonString = JsonUtil.getJsonFromMapObject(jsonMap);
			jsonMap = new HashMap<String, Object>();
			jsonMap = JsonUtil.getJsonFromString(HttpUtils.sendHttpPostRequest(wechat_url_notifyupdate, jsonString));
			result = jsonMap.get("result").toString();
		}
		log.info("【TCPSERVER】sendUpdateNotifyToWeChat End");
		return result;
	}
}
