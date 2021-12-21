package org.grain.net.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author laowu
 */
public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public static void run(String[] args) {
        new ChatServer(9000).run();
    }

    public void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("started");
            future.channel().closeFuture().sync();
            System.out.println("stop");
        } catch (Exception ignore) {

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
