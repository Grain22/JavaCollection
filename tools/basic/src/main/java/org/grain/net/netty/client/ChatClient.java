package org.grain.net.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author laowu
 */
public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void run(String[] args) {
        new ChatClient("localhost", 9000).run();
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            boolean close = false;
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());
            ChannelFuture connect = b.connect(host, port);
            connect.addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    channelFuture.channel().eventLoop().schedule(() -> {
                        System.out.println("re connect");
                        b.connect(host, port);
                    }, 10, TimeUnit.SECONDS);
                }
            });
            Channel channel = connect.sync().channel();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (!Thread.interrupted() && !close) {
                String s = input.readLine();
                if ("close".equals(s)) {
                    close = true;
                }
                channel.writeAndFlush(s + "\n");
            }
        } catch (Exception ignore) {
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
