package net.dandoes.minecraft.nodesupport;


import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@ChannelHandler.Sharable
public class NodeInteropChannelHandler extends ChannelInitializer<SocketChannel> {

    private static final Logger LOGGER = LogManager.getLogger(NodeInteropChannelHandler.class);

    private final NodeInteropServer interopServer;
    private final Set<NodeInteropServerHandler> handlers = new HashSet<>();

    public NodeInteropChannelHandler(final NodeInteropServer interopServer) {
        this.interopServer = interopServer;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        LOGGER.debug("NodeInteropServer.initChannel");
        final NodeInteropServerHandler handler = new NodeInteropServerHandler(this.interopServer, ch);
        ch.pipeline().addLast(handler);
        handlers.add(handler);
        ch.closeFuture().addListener((ChannelFutureListener) future -> {
            handlers.remove(handler);
            NodeInteropClientDisconnectEvent disconnectEvent = new NodeInteropClientDisconnectEvent(handler.getClient());
            MinecraftForge.EVENT_BUS.post(disconnectEvent);
        });
    }
}
