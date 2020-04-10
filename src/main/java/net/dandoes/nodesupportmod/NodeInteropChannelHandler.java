package net.dandoes.nodesupportmod;


import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@ChannelHandler.Sharable
public class NodeInteropChannelHandler extends ChannelInitializer<SocketChannel> {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropChannelHandler.class);

    private final Set<NodeInteropServerHandler> handlers = new HashSet<>();
    private final MinecraftServer server;

    public NodeInteropChannelHandler(MinecraftServer server) {
        this.server = server;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        LOGGER.debug("NodeInteropServer.initChannel");
        final NodeInteropServerHandler handler = new NodeInteropServerHandler(ch, server);
        ch.pipeline().addLast(handler);
        handlers.add(handler);
        ch.closeFuture().addListener((ChannelFutureListener) future -> {
            handlers.remove(handler);
        });
    }

    public void broadcast(Event event) {
        for (NodeInteropServerHandler handler : this.handlers) {
            handler.sendEvent(event);
        }
    }
}
