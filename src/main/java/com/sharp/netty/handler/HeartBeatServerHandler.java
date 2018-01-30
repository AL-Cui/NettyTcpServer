package com.sharp.netty.handler;

import com.sharp.netty.common.DeviceInfo;
import com.sharp.netty.common.KVStoreUtils;
import com.sharp.netty.common.StringUtil;
import com.sharp.netty.sessioncache.SessionCache;
import com.sharp.netty.utils.AgentUtil;
import com.sharp.netty.utils.Util;
import com.sharp.netty.utils.WifiConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);
    private final static Map<String, String> firstHeartBeatMap = new HashMap<>();
    public static AttributeKey<String> MACADDRESS = AttributeKey.valueOf("macAddress");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//        logger.info("->server:" + msg.toString());
        readXML(msg.toString(), ctx);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        sessionClosed(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionClosed(ctx);
    }

    private void sessionClosed(ChannelHandlerContext ctx) throws Exception {
        // 连接关闭
        if (ctx.attr(MACADDRESS).get() != null) {
            String mac = ctx.attr(MACADDRESS).get().toString();
            firstHeartBeatMap.remove(mac);
            this.sessionRemove(mac, ctx.channel().remoteAddress().toString());
            ctx.close();
        }
    }

    /***
     * 解析收到的数据
     * @param contents
     * @param ctx
     * @throws DocumentException
     */
    private void readXML(String contents, ChannelHandlerContext ctx) throws DocumentException {

        Document readDoc = DocumentHelper.parseText(contents);
        Element rootNode = readDoc.getRootElement();
        String mac;
        // 匹配节点：tcp_msg
        if (Util.NODE_TCP_MSG.equals(rootNode.getName())) {
            // 匹配节点（msg）内容
            if (Util.MSG_VALUE_HEARTBEAT.equals(rootNode.elementTextTrim(Util.NODE_MSG))) {   //收到机器端心跳包数据
                // mac address
                Element node = rootNode.element(Util.NODE_DATA);
                mac = node.elementTextTrim(Util.NODE_MAC_ADDRESS);
                //log.info("机器心跳中的mac地址"+mac);
                String wifiVersion = node.elementTextTrim(Util.NODE_VERSION);
                String machverVersion = node.elementTextTrim(Util.NODE_VERSIONMACHVER);
//                session.setAttribute("mac", mac);
                // 生成所需XML文件
                createXML(mac, ctx, wifiVersion, machverVersion);

            } else if (Util.MSG_VALUE_NOTIFY.equals(rootNode.elementTextTrim(Util.NODE_MSG))) {
                if (Util.MSG_VALUE_LINKRESET.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {    //收到从机器端解绑
                    // mac address
                    Iterator<Element> iterator = rootNode.elementIterator(Util.NODE_DATA);
                    while (iterator.hasNext()) {
                        Element macNode = iterator.next();
                        if (macNode != null) {
                            mac = macNode.elementTextTrim(Util.NODE_MAC_ADDRESS);
//                            ctx.channel().attr(new AttributeKey<>("mac"));
//                            SocketChannel.setAttribute("mac", mac);
                            // 解绑所有用户
                            AgentUtil.unbindAllUser(mac);
                            // 删除boxID
                            boxIdDelete(mac);
                        }
                    }
                } else if (Util.MSG_VALUE_UPDATE.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {     //收到机器发送更新的WIFI版本号，并回复
                    // data
                    Element node = rootNode.element(Util.NODE_DATA);
                    mac = node.elementTextTrim(Util.NODE_MAC_ADDRESS);
                    String update_flag = node.elementTextTrim(Util.NODE_UPDATEFLAG);
                    String version = node.elementTextTrim(Util.NODE_VERSION);

					/* 回复信息 */

                    // DocumentHelper提供了创建Document对象的方法
                    Document writeDoc = DocumentHelper.createDocument();
                    // 添加节点：tcp_msg
                    Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                    // 添加节点：msg
                    Element msg = root.addElement(Util.NODE_MSG);
                    // 设置节点信息
                    msg.setText(Util.MSG_VALUE_CTRLNOTIFY);
                    // 添加节点:cmd
                    Element cmd = root.addElement(Util.NODE_CMD);
                    cmd.setText(Util.CMD_VALUE_RECEIVE);
                    // 添加节点:data
                    Element data = root.addElement(Util.NODE_DATA);
                    // 添加节点:server_time
                    Element update = data.addElement(Util.NODE_UPDATEFLAG);
                    // 设置节点信息
                    update.setText(update_flag);
                    // 将document文档对象直接转换成字符串
                    logger.info("发送内容: " + writeDoc.asXML());
                    ByteBuf VALUE_UPDATE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML() + "\r\n",
                            CharsetUtil.UTF_8));
                    ctx.writeAndFlush(VALUE_UPDATE.duplicate());
					/* 回复信息 */

                    // 通知用户更新结果信息
//                    WeChatUtil.sendUpdateInfoToWeChat(mac, update_flag, version, "wifi");
                } else if (Util.MSG_VALUE_MACHVER.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {     //收到机器发送更新的主板版本号，并回复
                    // data
                    Element node = rootNode.element(Util.NODE_DATA);
                    mac = node.elementTextTrim(Util.NODE_MAC_ADDRESS);
                    String update_flag = node.elementTextTrim(Util.NODE_UPDATEFLAG);
                    String version = node.elementTextTrim(Util.NODE_VERSION);

					/* 回复信息 */

                    // DocumentHelper提供了创建Document对象的方法
                    Document writeDoc = DocumentHelper.createDocument();
                    // 添加节点：tcp_msg
                    Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
                    // 添加节点：msg
                    Element msg = root.addElement(Util.NODE_MSG);
                    // 设置节点信息
                    msg.setText(Util.MSG_VALUE_CTRLNOTIFY);
                    // 添加节点:cmd
                    Element cmd = root.addElement(Util.NODE_CMD);
                    cmd.setText(Util.CMD_VALUE_RECEIVEMACHVER);
                    // 添加节点:data
                    Element data = root.addElement(Util.NODE_DATA);
                    // 添加节点:server_time
                    Element update = data.addElement(Util.NODE_UPDATEFLAG);
                    // 设置节点信息
                    update.setText(update_flag);
                    // 将document文档对象直接转换成字符串
                    logger.info("发送内容: " + writeDoc.asXML());
                    ByteBuf VALUE_MACHVER = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML() + "\r\n", CharsetUtil.UTF_8));
                    ctx.writeAndFlush(VALUE_MACHVER.duplicate());
//                    session.write(writeDoc.asXML());

					/* 回复信息 */

                    // 通知用户更新结果信息
//                    WeChatUtil.sendUpdateInfoToWeChat(mac, update_flag, version, "purifier");
                }
            }

        }


    }

    /***
     * 编辑回复给WIFI的心跳包，并做SessionCache的更新和版本判断
     * @param mac
     * @param ctx
     * @param wifiVersion
     * @param machverVersion
     */
    private void createXML(String mac, ChannelHandlerContext ctx, String wifiVersion, String machverVersion) {

        logger.info("当前处理Mac地址 : " + mac);
        if (StringUtil.isNotBlank(mac)) {



            // DocumentHelper提供了创建Document对象的方法
            Document writeDoc = DocumentHelper.createDocument();
            // 添加节点：tcp_msg
            Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
            // 添加节点：msg
            Element msg = root.addElement(Util.NODE_MSG);
            // 设置节点信息
            msg.setText(Util.MSG_VALUE_HEARTBEATRES);
            // 添加节点:cmd
            Element cmd = root.addElement(Util.NODE_CMD);
            // 不再检查每个心跳包
            cmd.setText(Util.VALUE_NULL);
            // 添加节点:data
            Element data = root.addElement(Util.NODE_DATA);
            // 添加节点:server_time
            Element server_time = data.addElement(Util.NODE_SERVER_TIME);
            // 设置节点信息
            server_time.setText(String.valueOf(System.currentTimeMillis()));
            // 将document文档对象直接转换成字符串
//            logger.info("接受心跳后服务端发送的内容: " + writeDoc.asXML() + "  ToMac" + mac);
            ByteBuf HEARTBEATRES = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML() + "\r\n", CharsetUtil.UTF_8));
            ctx.channel().writeAndFlush(HEARTBEATRES.duplicate());
            ReferenceCountUtil.release(HEARTBEATRES);

            // 如果是第一次心跳包判断版本更新
            if (!firstHeartBeatMap.containsKey(mac)) {
                logger.info("ctx=" + ctx.toString());
                firstHeartBeatMap.put(mac, mac);
//                SessionCache.getInstance().save(mac, (SocketChannel) ctx.channel());
//                ctx.attr(MACADDRESS).set(mac);
                // 判断当前wifi版本是否需要更新
//                wifiUpdate(mac, wifiVersion);
//
//                purifierUpdate(mac, machverVersion);
            }
            //接收心跳包后更新SessionCache和ChannelHandlerContext的MACADDRESS属性
            String ipPortString = KVStoreUtils.getHashmapField(Util.KV_PORT_KEY, mac);
            if (!ctx.channel().remoteAddress().toString().equals(ipPortString)){
                SessionCache.getInstance().save(mac, (SocketChannel) ctx.channel());
                ctx.attr(MACADDRESS).set(mac);
            }
        }

    }

    /**
     * session关闭清除sessionCache
     */
    private void sessionRemove(String mac, String clientIp) {

        logger.info("连接关闭：" + mac);
        if (StringUtil.isNotBlank(mac)) {
            // 清除信息
            SessionCache.getInstance().remove(mac, clientIp);
//            changeStatus(mac, "0");
        }

    }

    /***
     * 发送解绑指令给WIFI模组
     * @param macAddress
     */
    private void boxIdDelete(String macAddress) {

        SocketChannel session = SessionCache.getInstance().isExists(macAddress);
        // 连接保持判断
        if (session != null) {

            // DocumentHelper提供了创建Document对象的方法
            Document writeDoc = DocumentHelper.createDocument();
            // 添加节点：tcp_msg
            Element root = writeDoc.addElement(Util.NODE_TCP_MSG);
            // 添加节点：msg ,并添加内容
            root.addElement(Util.NODE_MSG).setText(Util.MSG_VALUE_CTRLNOTIFY);
            // 添加节点 : cmd ,并添加内容
            root.addElement(Util.NODE_CMD).setText(Util.CMD_VALUE_BOXDELETE);
            logger.debug("发送信息：" + writeDoc.asXML());
            ByteBuf replyString = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML(), CharsetUtil.UTF_8));
            session.writeAndFlush(replyString.duplicate());
            ReferenceCountUtil.release(replyString);

        }

    }

    /**
     * wifi版本升级处理
     * wifiVersion--->WIFI通过心跳传上来的wifiversion
     * newWifiVersion--->通过Agent获取的数据库中的wifiVersion
     * nowWifiVersion--->通过配置文件获取的wifiVersion
     * @param mac
     */
    private void wifiUpdate(String mac, String wifiVersion) {
        logger.info("wifiUpdate Strat-->" + wifiVersion);

        DeviceInfo deviceInfo = AgentUtil.getDeviceInfoByMac(mac);
        logger.info("deviceInfo="+deviceInfo.toString());
        if (deviceInfo != null) {
            String newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind());
            String nowWifiVersion = deviceInfo.getWifiVersion();
            logger.info("wifiVersion="+wifiVersion+"newWifiVersion="+newWifiVersion+"newWifiVersion="+nowWifiVersion);
            if (StringUtil.isNotBlank(wifiVersion)) {
                if (!wifiVersion.equals(nowWifiVersion)) {
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setMacAddress(deviceInfo.getMacAddress());
                    deviceInfoInput.setWifiVersion(wifiVersion);
                   String resultFlag =  AgentUtil.updateDeviceInfo(deviceInfoInput, "wifi");
                   logger.info("resultFlag="+resultFlag);
                }
                //通知Agent那边需要更新
                if (StringUtil.isCompareTo(newWifiVersion, wifiVersion)) {
                    AgentUtil.sendUpdateMessage(mac, newWifiVersion, "wifi");
                }

            } else {
                if (!StringUtil.isNotBlank(nowWifiVersion)) {
                    String defaultVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_default_wifimoudle_ver");
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setMacAddress(deviceInfo.getMacAddress());
                    deviceInfoInput.setWifiVersion(defaultVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput, "wifi");
                    //替换Agent那边的接口
                    if (!newWifiVersion.equals(defaultVersion)) {
                        AgentUtil.sendUpdateMessage(mac, newWifiVersion, "wifi");
                    }
                } else {
                    //替换Agent那边的接口
                    if (StringUtil.isCompareTo(newWifiVersion, nowWifiVersion)) {
                        AgentUtil.sendUpdateMessage(mac, newWifiVersion, "wifi");
                    }
                }
            }
        }

        logger.info("wifiUpdate End");
    }

    /**
     * 主板版本升级处理
     * 主要版本相关变量同上
     * @param mac
     */
    private void purifierUpdate(String mac, String purifierVersion) {
        logger.info("purifierUpdate Strat-->" + purifierVersion);

        DeviceInfo deviceInfo = AgentUtil.getDeviceInfoByMac(mac);
        if (deviceInfo != null) {
            String newVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_PURIFIER");
            String nowVersion = deviceInfo.getPurifierVersion();
            if (StringUtil.isNotBlank(purifierVersion)) {
                if (!purifierVersion.equals(nowVersion)) {
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setMacAddress(deviceInfo.getMacAddress());
                    deviceInfoInput.setPurifierVersion(purifierVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput, "machine");
                }
                //替换Agent那边的接口
                if (StringUtil.isCompareTo(newVersion, purifierVersion)) {
                    AgentUtil.sendUpdateMessage(mac, newVersion, "machine");
                }

            } else {
                if (!StringUtil.isNotBlank(nowVersion)) {
                    String defaultVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_default_purifier_ver");
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setMacAddress(deviceInfo.getMacAddress());
                    deviceInfoInput.setPurifierVersion(defaultVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput, "machine");
                    //替换Agent那边的接口
                    if (!newVersion.equals(defaultVersion)) {
                        AgentUtil.sendUpdateMessage(mac, newVersion, "machine");
                    }
                } else {
                    if (StringUtil.isNotBlank(newVersion) && StringUtil.isNotBlank(nowVersion)) {
                        //替换Agent那边的接口
                        if (StringUtil.isCompareToUpdateVerson(newVersion, nowVersion)) {
                            AgentUtil.sendUpdateMessage(mac, newVersion, "machine");
                        }
                    }
                }
            }
        }

        logger.info("purifierUpdate End");
    }

//    /**
//     * 检查对面地址是否在黑名单中
//     *
//     * @param "session
//     *            连接session
//     * @return 是：true，否：false
//     */
//    private boolean checkIsBlocked(ChannelHandlerContext ctx) {
//        boolean ret = false;
//        String[] blockList = { "100.97" };
//
//        for (String blocked : blockList) {
//            if (ctx.channel().remoteAddress().toString().indexOf(blocked) > 0) {
//                ret = true;
//                break;
//            }
//        }
//
//        return ret;
//    }
}
