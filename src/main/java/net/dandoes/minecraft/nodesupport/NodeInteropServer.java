package net.dandoes.minecraft.nodesupport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodeInteropServer {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropServer.class);

    private final DedicatedServer server;
    private final int port;
    private final NodeInteropChannelHandler handler;

    public NodeInteropServer(final DedicatedServer server, int port) {
        this.server = server;
        this.handler = new NodeInteropChannelHandler(this);
        this.port = port;
    }

    public DedicatedServer getServer() {
        return server;
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
}
