package com.sharp.netty.handler;

import com.sharp.netty.common.DeviceBindInfo;
import com.sharp.netty.common.DeviceInfo;
import com.sharp.netty.common.StringUtil;
import com.sharp.netty.common.WeChatUtil;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServerHandler.class);
    private final static Map<String, String> firstHeartBeatMap = new HashMap<>();
    private static AttributeKey<String> MACADDRESS = AttributeKey.valueOf("macAddress");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        logger.info(simpleDateFormat.format(new Date()) + ctx.channel().remoteAddress() + "->server:" + msg.toString());
        readXML(msg.toString(), ctx);
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
        // 关闭通知
        if (ctx.attr(MACADDRESS).get() != null) {
            String mac = ctx.attr(MACADDRESS).get().toString();
            firstHeartBeatMap.remove(mac);
            this.sessionRemove(mac, ctx.channel().remoteAddress().toString());
            ctx.close();
        }
    }

    private void readXML(String contents, ChannelHandlerContext ctx) throws DocumentException {

        Document readDoc = DocumentHelper.parseText(contents);
        Element rootNode = readDoc.getRootElement();
        String mac;
        // 匹配节点：tcp_msg
        if (Util.NODE_TCP_MSG.equals(rootNode.getName())) {
            // 匹配节点（msg）内容
            if (Util.MSG_VALUE_HEARTBEAT.equals(rootNode.elementTextTrim(Util.NODE_MSG))) {
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
                if (Util.MSG_VALUE_LINKRESET.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {
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
                } else if (Util.MSG_VALUE_UPDATE.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {
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
                    ByteBuf VALUE_UPDATE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n",
                            CharsetUtil.UTF_8));
                    ctx.writeAndFlush(VALUE_UPDATE.duplicate());
//                    session.write(writeDoc.asXML());

					/* 回复信息 */

                    // 通知用户更新结果信息
//                    WeChatUtil.sendUpdateInfoToWeChat(mac, update_flag, version, "wifi");
                } else if (Util.MSG_VALUE_MACHVER.equals(rootNode.elementTextTrim(Util.NODE_CMD))) {
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
                    ByteBuf VALUE_MACHVER = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
                    ctx.writeAndFlush(VALUE_MACHVER.duplicate());
//                    session.write(writeDoc.asXML());

					/* 回复信息 */

                    // 通知用户更新结果信息
//                    WeChatUtil.sendUpdateInfoToWeChat(mac, update_flag, version, "purifier");
                }
            }

        }


    }

    private void createXML(String mac, ChannelHandlerContext ctx, String wifiVersion, String machverVersion) {

        logger.info("当前处理Mac地址 : " + mac);
        if (StringUtil.isNotBlank(mac)) {

            // 第一次心跳包
            if (!firstHeartBeatMap.containsKey(mac)) {
                logger.info("ctx=" + ctx.toString());
                SessionCache.getInstance().save(mac, (SocketChannel) ctx.channel());
                ctx.attr(MACADDRESS).set(mac);
                logger.info("长连接的Mac地址属性=" + ctx.attr(MACADDRESS).get().toString());
                firstHeartBeatMap.put(mac, mac);

                // 判断当前wifi版本是否需要更新
                wifiUpdate(mac, wifiVersion);

                purifierUpdate(mac, machverVersion);

                AgentUtil.getCityNameByIp(mac, ctx.channel().remoteAddress().toString().split(":")[0].substring(1));

                 //判断是否存在boxID和绑定关系
                if (Util.VALUE_STRING_ONE.equals(AgentUtil.checkBindingStatusByMac(mac))) {
                    // 改变设备状态为连接状态
                    changeStatus(mac, "1");
                }

                AgentUtil.smartSet(mac);
            }
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
            logger.info("接受心跳后服务端发送的内容: " + writeDoc.asXML() + "  ToMac" + mac);
            ByteBuf HEARTBEATRES = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(writeDoc.asXML()+"\r\n", CharsetUtil.UTF_8));
            ctx.channel().writeAndFlush(HEARTBEATRES.duplicate());
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

    /**
     * 改变微信状态
     *
     * @param mac
     * @param status
     */
    private void changeStatus(String mac, String status) {

        List<DeviceBindInfo> infos = AgentUtil.getDeviceBindInfoByMac(mac);
        List<String> openIDs = new ArrayList<String>();
        if (infos != null) {
            for (DeviceBindInfo info : infos) {
                if ("0".equals(info.getAdvanceBindFlg())) {
                    openIDs.add(info.getOpenId());
                }
            }
            if (openIDs.size() > 0) {
                WeChatUtil.sendConnStatusToWeChat(openIDs, infos.get(0).getDeviceId(), status);
            }
        }

    }

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

        }

    }

    /**
     * wifi版本升级处理
     *
     * @param mac
     */
    private void wifiUpdate(String mac, String wifiVersion) {
        logger.info("wifiUpdate Strat");

        DeviceInfo deviceInfo = AgentUtil.getDeviceInfoByMac(mac);
        if (deviceInfo != null) {
            String newWifiVersion = WifiConfig.getValue(deviceInfo.getDeviceKind());
            String nowWifiVersion = deviceInfo.getWifiVersion();
            if (StringUtil.isNotBlank(wifiVersion)) {
                if (!wifiVersion.equals(nowWifiVersion)) {
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setDeviceId(deviceInfo.getDeviceId());
                    deviceInfoInput.setWifiVersion(wifiVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput);
                }

                if (StringUtil.isCompareTo(newWifiVersion, wifiVersion)) {
                    WeChatUtil.sendUpdateNotifyToWeChat(mac, newWifiVersion, "wifi");
                }

            } else {
                if (!StringUtil.isNotBlank(nowWifiVersion)) {
                    String defaultVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_default_wifimoudle_ver");
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setDeviceId(deviceInfo.getDeviceId());
                    deviceInfoInput.setWifiVersion(defaultVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput);

                    if (!newWifiVersion.equals(defaultVersion)) {
                        WeChatUtil.sendUpdateNotifyToWeChat(mac, newWifiVersion, "wifi");
                    }
                } else {
                    if (StringUtil.isCompareTo(newWifiVersion, nowWifiVersion)) {
                        WeChatUtil.sendUpdateNotifyToWeChat(mac, newWifiVersion, "wifi");
                    }
                }
            }
        }

        logger.info("wifiUpdate End");
    }

    /**
     * purifer版本升级处理
     *
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
                    deviceInfoInput.setDeviceId(deviceInfo.getDeviceId());
                    deviceInfoInput.setPurifierVersion(purifierVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput);
                }

                if (StringUtil.isCompareTo(newVersion, purifierVersion)) {
                    WeChatUtil.sendUpdateNotifyToWeChat(mac, newVersion, "purifier");
                }

            } else {
                if (!StringUtil.isNotBlank(nowVersion)) {
                    String defaultVersion = WifiConfig.getValue(deviceInfo.getDeviceKind() + "_default_purifier_ver");
                    // 更新db中wifi的版本号
                    DeviceInfo deviceInfoInput = new DeviceInfo();
                    deviceInfoInput.setDeviceId(deviceInfo.getDeviceId());
                    deviceInfoInput.setPurifierVersion(defaultVersion);
                    AgentUtil.updateDeviceInfo(deviceInfoInput);

                    if (!newVersion.equals(defaultVersion)) {
                        WeChatUtil.sendUpdateNotifyToWeChat(mac, newVersion, "purifier");
                    }
                } else {
                    if (StringUtil.isNotBlank(newVersion) && StringUtil.isNotBlank(nowVersion)) {
                        if (StringUtil.isCompareToUpdateVerson(newVersion, nowVersion)) {
                            WeChatUtil.sendUpdateNotifyToWeChat(mac, newVersion, "purifier");
                        }
                    }
                }
            }
        }

        logger.info("purifierUpdate End");
    }

    /**
     * 检查对面地址是否在黑名单中
     *
     * @param "session
     *            连接session
     * @return 是：true，否：false
     */
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
