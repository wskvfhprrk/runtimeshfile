package com.hejz.runtime_sh_file.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 来源于chatGPT一个断线重连功能的Netty客户端程序示例。它将不断地尝试重新连接到服务器，如果连接被意外中断，将自动重新连接。
 */

//@Component
@Slf4j
public class NettyClient {
    private static final int MAX_RETRY = 5;
    private static final int READ_TIMEOUT_SECONDS = 120;

//    @PostConstruct
    public static void run() {
        String host = "localhost";
        int port = 9090;

        NioEventLoopGroup group = new NioEventLoopGroup();
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT_SECONDS, READ_TIMEOUT_SECONDS, READ_TIMEOUT_SECONDS, TimeUnit.SECONDS));
                                ch.pipeline().addLast(new ClientHandler());
                            }
                        });

                ChannelFuture future = bootstrap.connect(host, port).sync();
                log.info("客户端连接到" + host + ":" + port);

                String message = "0000";
                ByteBuf buffer = future.channel().alloc().buffer(message.length());
                buffer.writeBytes(HexConvert.hexStringToBytes(message));

                future.channel().writeAndFlush(buffer);
                log.info("发送到服务器的消息: " + message);

                future.channel().closeFuture().sync();
            } catch (Exception e) {
                System.err.println("无法连接到 " + host + ":" + port);
                retry++;
                try {
                    // Wait for 10 seconds before reconnecting
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("已达到最大重试次数，请退出。");
        group.shutdownGracefully();
    }

    private static class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            String message = buf.toString(io.netty.util.CharsetUtil.UTF_8);
            log.info("从服务器接收到消息: " + message);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleState state = ((IdleStateEvent) evt).state();
                switch (state) {
                    case READER_IDLE:
                        log.info("channel:{},空闲{}分钟无上报数据自动关闭通道！",ctx.channel().id().toString(),READ_TIMEOUT_SECONDS);
                        ctx.close();
                        break;
                    case WRITER_IDLE:
                        log.info("发送心跳包给客户端：00 00");
                        //根据检查频率和实际情况写空闲时发送心跳包给客户端——60秒,如果不存活的通道就不发送了
                        if (ctx.channel().isActive()) {
                            String message = "0000";
                            ByteBuf buffer = ctx.channel().alloc().buffer(message.length());
                            buffer.writeBytes(HexConvert.hexStringToBytes(message));
                            ctx.channel().writeAndFlush(buffer);
                        }
                        break;
                    case ALL_IDLE:
//                    log.info("读写都空闲");
                        break;
                }
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.err.println("连接丢失，正在重试连接。。。");
            ctx.channel().eventLoop().schedule(() -> run(), 10, TimeUnit.SECONDS);
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
