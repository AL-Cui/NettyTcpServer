package com.sharp.netty.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 解析JSON格式数据
 * 
 * @author Duo.Cui
 *
 */
public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	/**
	 * 以Map类型解析JSON
	 * 
	 * @param jsonString
	 *            JSON格式字符串
	 * @return Map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJsonFromString(String jsonString) {
		Map<String, Object> jsonObj = null;
		if (StringUtil.isNotBlank(jsonString)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonObj = mapper.readValue(jsonString, Map.class);
			} catch (IOException e) {
				logger.error("JsonUtil(getJsonFromString) error:" + e.toString());
			}
		}
		return jsonObj;
	}

	/**
	 * 以Map类型解析JSON
	 * 
	 * @param jsonString
	 *            JSON格式字符串
	 * @return List对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getJsonListFromString(String jsonString, Class<?> classname) {
		List result = new ArrayList();
		List<Map<String, String>> jsonObj = null;
		if (StringUtil.isNotBlank(jsonString)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonObj = mapper.readValue(jsonString, List.class);
				for (Map<String, String> jsonMap : jsonObj) {
					result.add(getBeanFromJson(getJsonFromMap(jsonMap), classname));
				}
			} catch (IOException e) {
				logger.error("JsonUtil(getJsonListFromString) error:" + e.toString());
			}
		}
		return result;
	}

	/**
	 * 以Map类型解析JSON
	 * 
	 * @param jsonString
	 *            JSON格式字符串
	 * @return List对象
	 */
	@SuppressWarnings("rawtypes")
	public static List getJsonListFromString(String jsonString) {
		List jsonObj = null;
		if (StringUtil.isNotBlank(jsonString)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonObj = mapper.readValue(jsonString, List.class);
			} catch (IOException e) {
				logger.error("JsonUtil(getJsonListFromString) error:" + e.toString());
			}
		}
		return jsonObj;
	}

	/**
	 * 以Object类型解析JSON
	 * 
	 * @param jsonString
	 *            JSON格式字符串
	 * @return Object对象
	 */
	public static Object getJsonObject(String jsonString) {
		Object jsonObj = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonObj = mapper.readValue(jsonString, Object.class);
		} catch (IOException e) {
			logger.error("JsonUtil(getJsonObject) error:" + e.toString());
		}
		return jsonObj;
	}

	/**
	 * 将Map<String,String>转换成json字符串
	 * 
	 * @param jsonMap
	 *            要转换的Map
	 * @return JSON格式字符串
	 */
	public static String getJsonFromMap(Map<String, String> jsonMap) {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writeValueAsString(jsonMap);
		} catch (JsonProcessingException e) {
			logger.error(e.toString());
		}

		return result;
	}

	/**
	 * 将Map<String,Object>转换成json字符串
	 * 
	 * @param jsonMap
	 *            要转换的Map
	 * @return JSON格式字符串
	 */
	public static String getJsonFromMapObject(Map<String, Object> jsonMap) {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
		try {
			result = mapper.writeValueAsString(jsonMap);
		} catch (JsonProcessingException e) {
			logger.error(e.toString());
		}

		return result;
	}

	/**
	 * 将json字符串转换成javabean对象
	 * 
	 * @param jsonString
	 *            要转换的json字符串
	 * @param classname
	 *            javabean的classname
	 * @return javabean对象
	 */
	public static Object getBeanFromJson(String jsonString, Class<?> classname) {
		Object result = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(jsonString, classname);
		} catch (JsonParseException e) {
			logger.error(e.toString());
		} catch (JsonMappingException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return result;
	}

	/**
	 * 将javabean对象转换成json字符串
	 * 
	 * @param javaBean
	 *            javaBean对象
	 * @return JSON格式字符串
	 */
	public static String getJsonFromBean(Object javaBean) {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writeValueAsString(javaBean);
		} catch (JsonProcessingException e) {
			logger.error(e.toString());
		}
		return result;
	}

	/**
	 * 将json字符串转换成boolean
	 * 
	 * @param "javaBean
	 *            javaBean对象
	 * @return JSON格式字符串
	 */
	public static boolean getBooleanFromJson(String jsonString) {
		boolean result = false;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(jsonString, Boolean.class);
		} catch (JsonParseException e) {
			logger.error(e.toString());
		} catch (JsonMappingException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return result;
	}

}
