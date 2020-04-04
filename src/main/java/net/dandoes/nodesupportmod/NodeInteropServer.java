package net.dandoes.nodesupportmod;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class NodeInteropServer {
    private static final Logger LOGGER = LogManager.getLogger();

    private int port;
    private final MinecraftServer server;

    private final Set<NodeInteropServerHandler> handlers = new HashSet<>();

    public NodeInteropServer(int port, MinecraftServer server) {
        this.port = port;
        this.server = server;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap(); // (2)
        final MinecraftServer server = this.server;
        final Set<NodeInteropServerHandler> handlers = this.handlers;
        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class) // (3)
            .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    final NodeInteropServerHandler handler = new NodeInteropServerHandler(ch, server);
                    ch.pipeline().addLast(handler);
                    handlers.add(handler);
                    ch.closeFuture().addListener((ChannelFutureListener) future -> {
                        handlers.remove(handler);
                    });
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128)          // (5)
            .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

        // Bind and start to accept incoming connections.
        ChannelFuture f = b.bind(port).sync(); // (7)
        LOGGER.info("listening on port " + port);

        // Wait until the server socket is closed.
        f.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        });
    }

    public void broadcast(Event event) {
        for (NodeInteropServerHandler handler : this.handlers) {
            handler.sendEvent(event);
        }
    }

}
