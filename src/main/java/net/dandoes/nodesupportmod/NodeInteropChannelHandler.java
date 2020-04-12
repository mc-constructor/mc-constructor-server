package net.dandoes.nodesupportmod;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@ChannelHandler.Sharable
public class NodeInteropChannelHandler extends ChannelInitializer<SocketChannel> {
    public static final SimpleCommandExceptionType CONTROLLER_NOT_CONNECTED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.nodesupportmod.controllernotconnected"));

    private static final Logger LOGGER = LogManager.getLogger(NodeInteropChannelHandler.class);
    private static NodeInteropChannelHandler instance;

    public static void checkHasHandler() throws CommandSyntaxException {
        if (instance.handlers.size() == 0) {
            throw CONTROLLER_NOT_CONNECTED.create();
        }
    }

    public static int sendToAllHandlers(Event event) {
        for (NodeInteropServerHandler handler : instance.handlers) {
            handler.sendEvent(event);
        }
        return instance.handlers.size();
    }

    private final Set<NodeInteropServerHandler> handlers = new HashSet<>();
    private final MinecraftServer server;

    public NodeInteropChannelHandler(MinecraftServer server) {
        this.server = server;
        instance = this;
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
