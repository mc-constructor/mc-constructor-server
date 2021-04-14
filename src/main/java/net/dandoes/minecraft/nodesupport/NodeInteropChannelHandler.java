package net.dandoes.minecraft.nodesupport;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@ChannelHandler.Sharable
public class NodeInteropChannelHandler extends ChannelInitializer<SocketChannel> {
    public static final SimpleCommandExceptionType CONTROLLER_NOT_CONNECTED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.dandoes_nodesupport.controllernotconnected"));

    private static final Logger LOGGER = LogManager.getLogger(NodeInteropChannelHandler.class);

    public void checkHasHandler(Event event) throws CommandSyntaxException {
        if (this.handlers.size() == 0) {
            if (event instanceof NodeInteropCommandEvent || event instanceof NodeInteropGameClientEvent) {
                throw CONTROLLER_NOT_CONNECTED.create();
            }
        }
    }

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

    public void broadcast(Event event) throws CommandSyntaxException {
        this.checkHasHandler(event);
        for (NodeInteropServerHandler handler : this.handlers) {
            handler.sendEvent(event);
        }
    }
}
