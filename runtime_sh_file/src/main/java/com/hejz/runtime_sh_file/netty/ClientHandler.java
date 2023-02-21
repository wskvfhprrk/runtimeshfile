package com.hejz.runtime_sh_file.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author:hejz 75412985@qq.com
 * @create: 2023-02-21 09:25
 * @Description:
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state){
                case READER_IDLE:
                    log.info("读取器空闲,重新部署服务器");
                    run();
                    break;
                case WRITER_IDLE:
                    log.info("写入程序空闲发送心跳包");
                    //给服务器心跳
                    ByteBuf bufff = Unpooled.buffer();
                    bufff.writeBytes(HexConvert.hexStringToBytes("0000"));
                    ctx.writeAndFlush(bufff);
                    break;
                case ALL_IDLE:
                    log.info("全部闲置");
                    break;
            }
        }else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ByteBuf byteBuf =(ByteBuf) o;
        int readableBytes = byteBuf.readableBytes();
        byte[] bytes = new byte[readableBytes];
        byteBuf.readBytes(bytes);
        String hex = HexConvert.BinaryToHexString(bytes);
        log.info("服务器响应的信息:{}",hex);
    }

    private void run(){
        try {
            log.info("项目部署………………………………………………");
            Process process = Runtime.getRuntime().exec("sh /root/start.sh" );
            InputStreamReader ips = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(ips);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
