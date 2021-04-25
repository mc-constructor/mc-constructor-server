package net.dandoes.minecraft.nodesupport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NodeInteropServer {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropServer.class);

    private final int port;
    private final NodeInteropChannelHandler handler;
    private final CompletableFuture<DedicatedServer> server;

    public NodeInteropServer(final DedicatedServer server, int port) {
        this.handler = new NodeInteropChannelHandler(this);
        this.port = port;
        this.server = new CompletableFuture<>();
        try {
            server.getWorld(DimensionType.OVERWORLD);
            this.server.complete(server);
        } catch (IllegalArgumentException ex) {
            MinecraftForge.EVENT_BUS.addListener((final FMLServerStartingEvent event) -> this.server.complete(server));
        }
    }

    public DedicatedServer getServer() {
        try {
            return this.server.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
