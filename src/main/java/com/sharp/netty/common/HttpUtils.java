package com.sharp.netty.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

/**
 * 发送HTTP请求的工具类
 * 
 * @author Pactera
 *
 */
public class HttpUtils {

	private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

	private static final int HTTP_OK = 200;

	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_DELETE = "DELETE";

	/**
	 * 发送各类HTTP请求
	 * 
	 * @param request
	 *            HTTP请求
	 * @param method
	 *            请求方式，用于log输出
	 * @return 所得回复内容
	 */
	private static String sendHttpRequest(HttpUriRequest request, String method) {

		String result = "";

		HttpClient client = HttpClientBuilder.create().build();
		InputStreamReader inputReader = null;
		try {
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HTTP_OK) {
				inputReader = new InputStreamReader(response.getEntity().getContent());
				BufferedReader in = new BufferedReader(inputReader);
				String line = "";
				while ((line = in.readLine()) != null) {
					result += line;
				}
				log.info(MessageFormat.format("HTTP {1} response {0} ", result, method));
			} else {
				log.info(MessageFormat.format("HTTP {1} response code[{0}] ",
						response.getStatusLine().getStatusCode(), method));
			}
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			if (inputReader != null) {
				try {
					inputReader.close();
				} catch (IOException e) {
					log.error(e.toString());
				}
			}
		}

		return result;
	}

	/**
	 * 发送HTTP GET请求
	 * 
	 * @param url
	 *            请求URL
	 * @return 所得回复内容
	 */
	public static String sendHttpGetRequest(String url) {

		HttpGet request = new HttpGet(url);

		log.info(MessageFormat.format("send HTTP GET reqeust to [{0}]", url));

		return sendHttpRequest(request, HTTP_METHOD_GET);
	}

	/**
	 * 发送HTTP POST请求
	 * 
	 * @param url
	 *            请求URL
	 * @param body
	 *            请求实体
	 * @return 所得回复内容
	 */
	public static String sendHttpPostRequest(String url, String body) {

		HttpPost request = new HttpPost(url);

		try {
			StringEntity entity;
			entity = new StringEntity(body, "UTF-8");
			entity.setContentType("application/json");
			entity.setContentEncoding("UTF-8");
			request.setEntity(entity);

			log.info(MessageFormat.format("send HTTP POST reqeust to [{0}],content[{1}]", url, body));

			return sendHttpRequest(request, HTTP_METHOD_POST);

		} catch (Exception e) {
			log.error(e.toString());
			return null;
		}
	}

	/**
	 * 发送HTTP PUT请求
	 * 
	 * @param url
	 *            请求URL
	 * @param body
	 *            请求实体
	 * @return 所得回复内容
	 */
	public static String sendHttpPutRequest(String url, String body) {

		HttpPut request = new HttpPut(url);

		try {
			StringEntity entity;
			entity = new StringEntity(body, "UTF-8");
			entity.setContentType("application/json");
			entity.setContentEncoding("UTF-8");
			request.setEntity(entity);

			log.info(MessageFormat.format("send HTTP PUT reqeust to [{0}],content[{1}]", url, body));

			return sendHttpRequest(request, HTTP_METHOD_PUT);

		} catch (Exception e) {
			log.error(e.toString());
			return null;
		}
	}

	/**
	 * 发送HTTP DELETE请求
	 * 
	 * @param url
	 *            请求URL
	 * @return 所得回复内容
	 */
	public static String sendHttpDeleteRequest(String url) {

		HttpDelete request = new HttpDelete(url);

		log.info(MessageFormat.format("send HTTP DELETE reqeust to [{0}],content[{1}]", url));

		return sendHttpRequest(request, HTTP_METHOD_DELETE);

	}

	/**
	 * 用于给请求附加参数
	 * 
	 * @param param
	 *            参数名称
	 * @param value
	 *            参数值
	 * @return 参数对字符串
	 */
	public static String addUrlParamPair(String param, String value) {

		String ret = param + "=" + value + "&";
		return ret;
	}

}
