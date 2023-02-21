package com.hejz.runtime_sh_file.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SystemClient {

    public static void main(String[] args) throws InterruptedException {
        run();
    }
    public static void run() throws InterruptedException {
        //配置客户端的线程组，客户端只有一个线程组，服务端是EventLoopGroup bossGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //放入自己的业务Handler
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(180, 60, 180, TimeUnit.SECONDS));
                            pipeline.addLast(new ClientHandler());
                        }
                    });
            //发起异步连接操作，同步阻等待结果
            ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();
//            ChannelFuture future = bootstrap.connect("ngrok.xiaomiqiu123.top", 38299).sync();
            try {
                start(future);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        } finally {
            //释放NIO线程组
            group.shutdownGracefully();
        }
    }


    private static void start(ChannelFuture future) throws IOException {
    }



}
