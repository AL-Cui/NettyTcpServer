package sharp.water.tcp;

import com.sharp.netty.common.*;
import com.sharp.netty.sessioncache.SessionCache;
import com.sharp.netty.utils.AgentUtil;
import com.sharp.netty.utils.Util;
import com.sharp.netty.utils.WifiConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author Duo.Cui
 * 供Agent调用的接口
 */
@RestController
public class impl {
    private static final Logger log = LoggerFactory.getLogger(impl.class);

    /***
     * 设备控制接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/deviceControl", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String deviceControl(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        log.info("【TCPSERVER】deviceControl Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        ByteBuf replyString = null;
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");

                log.info(MessageFormat.format("[deviceControl] invoked, PARAM: macAddress[{0}]", macAddress));
                SocketChannel session = SessionCache.getInstance().isExists(macAddress);
                // 连接保持判断
                if (session != null) {
                    try {
                        // DocumentHelper提供了创建Document对象的方法
                        Document writeDoc = DocumentHelper.createDocument();
                        // 添加节点：tcp_msg
                        Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                        // 添加节点：msg ,设置节点信息
                        root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
                        // 添加节点:cmd ,设置节点信息
                        root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE_CHECKCMD);
                        log.info("控制发送信息：" + writeDoc.asXML());
                        replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                        session.writeAndFlush(replyString.duplicate());
                        
                        // 正常状态
                        result.put("result", Util.VALUE_STRING_ONE);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        result.put("result", Util.VALUE_STRING_ZERO);
                    }
                } else {
                    // 异常状态
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            ReferenceCountUtil.release(replyString);
        }
        log.info("【TCPSERVER】deviceControl End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 查询设备是否在线的接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/isConnected", method = RequestMethod.GET)
    public String connected(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】connected Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");
                log.info(MessageFormat.format("[connected] invoked, PARAM: macAddress[{0}]", macAddress));
                if (SessionCache.getInstance().isExists(macAddress) != null) {
                    // 存在
                    result.put("result", Util.VALUE_STRING_ONE);
                } else {
                    // 不存在
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("【TCPSERVER】connected End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 设备绑定接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/boxIdCreate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String boxIdCreate(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        log.info("【TCPSERVER】boxIdCreate Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        ByteBuf replyString = null;
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");
                log.info(MessageFormat.format("[boxIdCreate] invoked, PARAM: macAddress[{0}]", macAddress));
                // 保存客户端的会话session
                //要换
                SocketChannel session = SessionCache.getInstance().isExists(macAddress);
//                SocketChannel session = SessionCache.getInstance().isExists(macAddress).getSocketChannel();
                if (session != null) {
                    // DocumentHelper提供了创建Document对象的方法
                    Document writeDoc = DocumentHelper.createDocument();
                    // 添加节点：tcp_msg
                    Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                    // 添加节点：msg,设置节点信息
                    root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
                    // 添加节点:cmd,设置节点信息
                    root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE);
                    // 将document文档对象直接转换成字符串
                    replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                    session.writeAndFlush(replyString.duplicate());

                    result.put("result", Util.VALUE_STRING_ONE);

                    log.info(MessageFormat.format("send message to[{0}] MAC[{1}] content:{2}", session
                            .remoteAddress().toString(), macAddress, writeDoc.asXML()));
                } else {
                    // 异常状态
                    log.info("机器处于异常状态，session不存在");
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                log.info("appSecret不对");
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            ReferenceCountUtil.release(replyString);
        }
        log.info("【TCPSERVER】boxIdCreate End,result=" + result);
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 设备解绑接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/boxIdDelete", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String boxIdDelete(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】boxIdDelete Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        ByteBuf replyString = null;
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");
                log.info(MessageFormat.format("[boxIdDelete] invoked, PARAM: macAddress[{0}]", macAddress));
                SocketChannel session = SessionCache.getInstance().isExists(macAddress);
                // 连接保持判断
                if (session != null) {
                    try {
                        // DocumentHelper提供了创建Document对象的方法
                        Document writeDoc = DocumentHelper.createDocument();
                        // 添加节点：tcp_msg
                        Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                        // 添加节点：msg ,并添加内容
                        root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
                        // 添加节点 : cmd ,并添加内容
                        root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE_BOXDELETE);
                        // log.info("发送信息：" + writeDoc.asXML());
                        replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                        session.writeAndFlush(replyString.duplicate());

                        log.info(MessageFormat.format("send message to[{0}] MAC[{1}] content:{2}", session
                                .remoteAddress().toString(), macAddress, writeDoc.asXML()));
                        // 正常状态
                        result.put("result", Util.VALUE_STRING_ONE);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        result.put("result", Util.VALUE_STRING_ZERO);
                    }
                } else {
                    // 异常状态
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            ReferenceCountUtil.release(replyString);
        }
        log.info("【TCPSERVER】boxIdDelete End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 通知WIFI版本更新的接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/updateVersion", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateVersion(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】updateVersion Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        ByteBuf replyString = null;
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");
                String autoFlg = servletRequest.getParameter("autoFlg");
                log.info(MessageFormat.format("[updateVersion] invoked, PARAM: macAddress[{0}]", macAddress));
                // 保存客户端的会话session
                SocketChannel session = SessionCache.getInstance().isExists(macAddress);
                if (session != null) {
                    String dateUrl = "";
                    String version = "";
                    String newWifiVersion = "";
                    boolean isNeedFlg = false;
                    // 获取设备型号
                    String deviceKind = AgentUtil.getDeviceInfoByMac(macAddress).getDeviceKind();
                    DeviceInfo deviceInfo = AgentUtil.getDeviceInfoByMac(macAddress);
                    if (deviceInfo != null) {
                        // 更新文件下载URL设置
                        if (Util.VALUE_STRING_ONE.equals(autoFlg)) {
                            dateUrl = WifiConfig.getValue(deviceKind + "_wifimoudle_autourl");
                            newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind());
                        } else {
                            dateUrl = WifiConfig.getValue(deviceKind + "_wifimoudle_handurl");
                            newWifiVersion = WifiConfig.getValue("M_" + deviceInfo.getDeviceKind());
                        }

                        String nowWifiVersion = deviceInfo.getWifiVersion();

                        // 版本check
                        if (StringUtil.isNotBlank(nowWifiVersion)) {
                            if (StringUtil.isCompareTo(newWifiVersion, nowWifiVersion)) {
                                // 需要升级
                                isNeedFlg = true;
                                version = newWifiVersion;
                            }
                        }
                    }

                    if (isNeedFlg) {
                        // 发送升级命令
                        // DocumentHelper提供了创建Document对象的方法
                        Document writeDoc = DocumentHelper.createDocument();
                        // 添加节点：tcp_msg
                        Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                        // 添加节点：msg,设置节点信息
                        root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
                        // 添加节点:cmd,设置节点信息
                        root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE_UPDATEVERSION);
                        // 添加节点 : data ,并添加内容
                        Element data = root.addElement(Util.NODE_DATA);
                        // 添加节点:url
                        data.addElement(Util.NODE_URL).setText(dateUrl);
                        // 添加节点:version_number
                        data.addElement(Util.NODE_VERSION).setText(version);
                        // 将document文档对象直接转换成字符串
                        replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                        session.writeAndFlush(replyString.duplicate());


                        log.info(MessageFormat.format("send message to[{0}] MAC[{1}] content:{2}", session
                                .remoteAddress().toString(), macAddress, writeDoc.asXML()));
                    }
                    // 正常状态
                    result.put("result", Util.VALUE_STRING_ONE);
                } else {
                    // 异常状态
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            ReferenceCountUtil.release(replyString);
        }
        log.info("【TCPSERVER】updateVersion End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 核实WIFI是否需要更新的接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/isNeedUpdate", method = RequestMethod.GET)
    public String checkWifiVer(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】checkWifiVer Start");
        Map<String, String> result = new HashMap<>();
        boolean resultFlg = false;
        String appSecret = servletRequest.getParameter("appSecret");
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String openId = servletRequest.getParameter("openId");
                String autoFlg = servletRequest.getParameter("autoFlg");
                List<DeviceBindInfo> deviceBindInfoList = AgentUtil.getDeviceBindInfoByOpenId(openId);
                for (DeviceBindInfo deviceBindInfo : deviceBindInfoList) {
                    if ("1".equals(deviceBindInfo.getFirstBindFlg())) {
                        String deviceId = deviceBindInfo.getDeviceId();
                        DeviceInfo deviceInfo = AgentUtil.getDeviceInfo(deviceId);
                        if (deviceInfo != null) {
                            String newWifiVersion = "";
                            String nowWifiVersion = deviceInfo.getWifiVersion();
                            // 更新文件下载URL设置
                            if (Util.VALUE_STRING_ONE.equals(autoFlg)) {
                                newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind());
                            } else {
                                newWifiVersion = WifiConfig.getValue("M_" + deviceInfo.getDeviceKind());
                            }

                            // 版本check
                            if (StringUtil.isNotBlank(nowWifiVersion)) {
                                if (StringUtil.isCompareTo(newWifiVersion, nowWifiVersion)) {
                                    // 需要升级
                                    result.put(deviceId, newWifiVersion);
                                    resultFlg = true;
                                }
                            }
                        }
                    }
                }

                if (resultFlg) {
                    result.put("result", Util.VALUE_STRING_ONE);
                } else {
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("【TCPSERVER】checkWifiVer End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 通知主板固件更新的接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/machverVersion", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String machverVersion(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】machverVersion Start");
        Map<String, String> result = new HashMap<>();
        String appSecret = servletRequest.getParameter("appSecret");
        ByteBuf replyString = null;
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String macAddress = servletRequest.getParameter("macAddress");
                String autoFlg = servletRequest.getParameter("autoFlg");
                log.info(MessageFormat.format("[machverVersion] invoked, PARAM: macAddress[{0}]", macAddress));
                // 保存客户端的会话session
                SocketChannel session = SessionCache.getInstance().isExists(macAddress);
                if (session != null) {
                    String dateUrl = "";
                    String version = "";
                    String newWifiVersion = "";
                    boolean isNeedFlg = false;
                    // 获取设备型号
                    String deviceKind = AgentUtil.getDeviceInfoByMac(macAddress).getDeviceKind();
                    DeviceInfo deviceInfo = AgentUtil.getDeviceInfoByMac(macAddress);
                    if (deviceInfo != null) {
                        // 更新文件下载URL设置
                        if (Util.VALUE_STRING_ONE.equals(autoFlg)) {
                            dateUrl = WifiConfig.getValue(deviceKind + "_purifier_autourl");
                            newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_PURIFIER");
                        } else {
                            dateUrl = WifiConfig.getValue(deviceKind + "_purifier_handurl");
                            newWifiVersion = WifiConfig.getValue("M_" + deviceInfo.getDeviceKind() + "_PURIFIER");
                        }

                        String nowWifiVersion = deviceInfo.getPurifierVersion();

                        // 版本check
                        if (StringUtil.isNotBlank(nowWifiVersion)) {
                            if (StringUtil.isCompareToUpdateVerson(newWifiVersion, nowWifiVersion)) {
                                // 需要升级
                                isNeedFlg = true;
                                version = newWifiVersion;
                            }
                        }
                    }

                    if (isNeedFlg) {
                        // 发送升级命令
                        // DocumentHelper提供了创建Document对象的方法
                        Document writeDoc = DocumentHelper.createDocument();
                        // 添加节点：tcp_msg
                        Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                        // 添加节点：msg,设置节点信息
                        root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
                        // 添加节点:cmd,设置节点信息
                        root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE_UPDATEMACHVER);
                        // 添加节点 : data ,并添加内容
                        Element data = root.addElement(Util.NODE_DATA);
                        // 添加节点:url
                        data.addElement(Util.NODE_URL).setText(dateUrl);
                        // 添加节点:version_number
                        data.addElement(Util.NODE_VERSION).setText(version);
                        // 将document文档对象直接转换成字符串
                        replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                        session.writeAndFlush(replyString.duplicate());


                        log.info(MessageFormat.format("send message to[{0}] MAC[{1}] content:{2}", session
                                .remoteAddress().toString(), macAddress, writeDoc.asXML()));
                    }
                    // 正常状态
                    result.put("result", Util.VALUE_STRING_ONE);
                } else {
                    // 异常状态
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                // 未经过认证
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }finally {
            ReferenceCountUtil.release(replyString);
        }
        log.info("【TCPSERVER】machverVersion End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 核实主板固件是否需要更新的接口
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/isMachverUpdate", method = RequestMethod.GET)
    public String isMachverUpdate(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        log.info("【TCPSERVER】isMachverUpdate Start");
        Map<String, String> result = new HashMap<>();
        boolean resultFlg = false;
        String appSecret = servletRequest.getParameter("appSecret");
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String openId = servletRequest.getParameter("openId");
                String autoFlg = servletRequest.getParameter("autoFlg");
                List<DeviceBindInfo> deviceBindInfoList = AgentUtil.getDeviceBindInfoByOpenId(openId);
                for (DeviceBindInfo deviceBindInfo : deviceBindInfoList) {
                    if ("1".equals(deviceBindInfo.getFirstBindFlg())) {
                        String deviceId = deviceBindInfo.getDeviceId();
                        DeviceInfo deviceInfo = AgentUtil.getDeviceInfo(deviceId);
                        if (deviceInfo != null) {
                            String newWifiVersion = "";
                            String nowWifiVersion = deviceInfo.getPurifierVersion();
                            // 更新文件下载URL设置
                            if (Util.VALUE_STRING_ONE.equals(autoFlg)) {
                                newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_PURIFIER");
                            } else {
                                newWifiVersion = WifiConfig.getValue("M_" + deviceInfo.getDeviceKind() + "_PURIFIER");
                            }
                            log.info("newWifiVersion-->" + newWifiVersion);
                            log.info("nowWifiVersion-->" + nowWifiVersion);
                            // 版本check
                            if (StringUtil.isNotBlank(nowWifiVersion)) {
                                if (StringUtil.isCompareToUpdateVerson(newWifiVersion, nowWifiVersion)) {
                                    // 需要升级
                                    result.put(deviceId, newWifiVersion);
                                    log.info("deviceId-->" + deviceId);
                                    resultFlg = true;
                                }
                            }
                        }
                    }
                }

                if (resultFlg) {
                    result.put("result", Util.VALUE_STRING_ONE);
                } else {
                    result.put("result", Util.VALUE_STRING_ZERO);
                }
            } else {
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("【TCPSERVER】isMachverUpdate End");
        return JsonUtil.getJsonFromMap(result);
    }

    /***
     * 获取所有在线的设备
     * @param servletRequest
     * @param servletResponse
     * @return result=0-->失败/1-->成功
     */
    @RequestMapping(value = "/socket/getOnlineDevice", method = RequestMethod.GET)
    public String getAllOnlineDevice(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("【TCPSERVER】getAllOnlineDevice Start");
        Map<String, String> result = new HashMap<>();
        String devicelist = "";
        String appSecret = servletRequest.getParameter("appSecret");
        try {
            if (Util.APP_SECRET.equals(appSecret)) {
                String localIp = servletRequest.getParameter("localIp");
                Map<String, String> onlineMap = KVStoreUtils.getAllField(Util.KV_PORT_KEY);
                for (String key : onlineMap.keySet()) {
                    if (onlineMap.get(key).contains(localIp)) {
                        if (StringUtil.isNotBlank(devicelist)) {
                            devicelist = devicelist + ";" + key;
                        } else {
                            devicelist = key;
                        }
                    }
                }

                if (StringUtil.isNotBlank(devicelist)) {
                    result.put("result", devicelist);
                } else {
                    result.put("result", Util.VALUE_STRING_ZERO);
                }

            } else {
                result.put("result", Util.VALUE_STRING_ZERO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("【TCPSERVER】getAllOnlineDevice End");
        return JsonUtil.getJsonFromMap(result);
    }
}

