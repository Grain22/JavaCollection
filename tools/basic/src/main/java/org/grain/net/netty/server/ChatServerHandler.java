package org.grain.net.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.grain.net.netty.constants.Strings;

import static org.grain.net.netty.constants.Strings.endString;

/**
 * @author laowu
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        try {
            Channel channel = ctx.channel();
            channels.forEach(a -> {
                if (channel != a) {
                    a.writeAndFlush(channel.remoteAddress() + " " + msg + Strings.endString);
                } else {
                    a.writeAndFlush("me: " + msg + Strings.endString);
                }
            });
        } catch (Exception ignore) {

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        channels.forEach(a -> {
            if (channel != a) {
                a.writeAndFlush(channel.remoteAddress() + " connected" + endString);
            }
        });
        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.forEach(a -> {
            if (a != channel) {
                a.writeAndFlush(channel.remoteAddress() + " " + "disconnected" + endString);
            }
        });
        channels.remove(channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
