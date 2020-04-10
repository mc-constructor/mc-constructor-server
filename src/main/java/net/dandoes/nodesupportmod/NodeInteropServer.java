package net.dandoes.nodesupportmod;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodeInteropServer {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropServer.class);

    private int port;
    private final NodeInteropChannelHandler handler;

    public NodeInteropServer(int port, MinecraftServer server) {
        this.port = port;
        this.handler = new NodeInteropChannelHandler(server);
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap(); // (2)

        b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class) // (3)
            .childHandler(this.handler)
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
        this.handler.broadcast(event);
    }

}
