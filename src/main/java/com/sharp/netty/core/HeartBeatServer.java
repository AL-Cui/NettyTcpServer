package com.sharp.netty.core;

import com.sharp.netty.handler.HeartBeatServerHandler;
import com.sharp.netty.listener.AcceptorIdleStateTrigger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

public class HeartBeatServer {
    private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
    private int port = 2001;
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatServer.class);

//    public HeartBeatServer(int port) {
//        this.port = port;
//    }

    static double coefficient = 0.8;
    static int numberOfCores = Runtime.getRuntime().availableProcessors();
    static int poolSize = (int) (numberOfCores / (1 - coefficient));  //poolsize是处理Handler的线程池数量

    static final EventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(poolSize);

    public void start() {

        String osName = System.getProperty("os.name");
        logger.info("操作系统是" + osName);
        if (osName.equals("Linux")) {
            EpollEventLoopGroup bossGroup = new EpollEventLoopGroup(); //这里可以传入参数，默认是cpu核数*2
            EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(); //这里可以传入参数，默认是cpu核数*2
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(EpollServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                initSocketChannel(socketChannel);
                            }
                        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture future = serverBootstrap.bind(port).sync();
                future.channel().closeFuture().sync();
                logger.info("Server start listen at " + port);
            } catch (Exception e) {
                logger.error("启动端口监听时出错" + e.toString());
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } else {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                initSocketChannel(socketChannel);
                            }
                        }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture future = serverBootstrap.bind(port).sync();
                future.channel().closeFuture().sync();
                logger.info("Server start listen at " + port);

            } catch (Exception e) {
                logger.error("启动端口监听时出错" + e.toString());
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }

    }

    private void initSocketChannel(SocketChannel socketChannel) {
        SSLContext sslContext = initSSLContext();
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setNeedClientAuth(false);
        socketChannel.pipeline().addFirst("ssl", new SslHandler(sslEngine));
        socketChannel.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
        socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
        socketChannel.pipeline().addLast("decode", new StringDecoder());
        socketChannel.pipeline().addLast("encode", new StringEncoder());
        socketChannel.pipeline().addLast(eventExecutorGroup, new HeartBeatServerHandler());
        socketChannel.pipeline().addLast(idleStateTrigger);
    }

    private SSLContext initSSLContext() {
        logger.info("initSSLContext初始化");
        SSLContext sslContext = null;
        try {
            final BASE64Decoder decoder = new BASE64Decoder();
//            String pass = new String(decoder.decodeBuffer("MTExMTEx"), "UTF-8");
            String pass = new String(decoder.decodeBuffer("aWRzYmcxMjMu"), "UTF-8");

            KeyStore ks = KeyStore.getInstance("JKS");
            InputStream ksInputStream = HeartBeatServer.class.getResourceAsStream("/blink_server.jks");
            ks.load(ksInputStream, pass.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, pass.toCharArray());
            sslContext = SSLContext.getInstance("TLSV1.2");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        if (sslContext != null) {
            logger.debug("sslContext初始化成功");
        }
        return sslContext;
    }

}
