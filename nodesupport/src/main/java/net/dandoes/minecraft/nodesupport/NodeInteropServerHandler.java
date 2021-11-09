package net.dandoes.minecraft.nodesupport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import net.dandoes.minecraft.nodesupport.event.NodeInteropClientSubscriptionCommandEvent;
import net.dandoes.minecraft.nodesupport.event.NodeInteropCommandEvent;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ChannelHandler.Sharable
public class NodeInteropServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropServerHandler.class);

    private static final String MSG_PREFIX_INTEROP = "interop:";

    private final NodeInteropServer interopServer;
    private final SocketChannel channel;
    private final StringBuilder buffer = new StringBuilder();
    private boolean isReady = false;
    private final char newline = "\n".charAt(0);
    private final Lock bufferLock = new ReentrantLock();
    private NodeInteropClient client;
    private String delimiter;

    public NodeInteropServerHandler(NodeInteropServer interopServer, SocketChannel channel) {
        this.interopServer = interopServer;
        this.channel = channel;
        LOGGER.debug("new net.dandoes.minecraft.nodesupport.NodeInteropServerHandler");
    }

    public DedicatedServer getServer() {
        return this.interopServer.getServer();
    }

    public NodeInteropClient getClient() {
        return client;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        final ByteBuf in = (ByteBuf) msg;
        try {
            this.bufferLock.lock();
            while (in.isReadable()) {
                final char c = (char) in.readByte();
                if (!this.isReady && c == this.newline) {
                    this.delimiter = this.buffer.toString();
                    this.isReady = true;
                    this.buffer.delete(0, this.buffer.length());
                    LOGGER.debug("got delimiter: " + this.delimiter);
                    try {
                        final ChannelWriter writer = new ChannelWriter(this.channel, this.delimiter);
                        this.client = new NodeInteropClient(writer);
                        this.channel.writeAndFlush(Unpooled.copiedBuffer(this.delimiter, CharsetUtil.UTF_8)).sync();
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                    continue;
                }
                this.buffer.append(c);
            }
            LOGGER.debug("done reading channel: " + this.buffer);
        } finally {
            this.bufferLock.unlock();
            ReferenceCountUtil.release(msg);
        }
        this.checkBuffer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
//        ctx.close();
    }

    private void checkBuffer() throws Exception {
        final int endMessageIndex = this.buffer.indexOf(this.delimiter);
        if (endMessageIndex < 0) {
            return;
        }
        final List<String> messages = this.getNextFromBuffer();
        for (String message : messages) {
            this.handleMessage(message);
        }
    }

    private List<String> getNextFromBuffer() {
        final List<String> messages = new ArrayList<>();
        // multiple messages can come in a single "chunk" / reading session. it's expensive to get the lock, so get all
        // messages in the buffer at once
        try {
            this.bufferLock.lock();
            int endMessageIndex = this.buffer.indexOf(this.delimiter);
            while (endMessageIndex >= 0) {
                messages.add(this.buffer.substring(0, endMessageIndex));
                this.buffer.delete(0, endMessageIndex + this.delimiter.length());
                endMessageIndex = this.buffer.indexOf(this.delimiter);
            }
            return messages;
        }
        finally {
            this.bufferLock.unlock();
        }
    }

    private void handleMessage(final String message) throws Exception {
        final String[] parts = message.split("\n");
        if (parts.length > 3) {
            throw new Exception("Unexpected message length");
        }
        LOGGER.debug("handleIncomingMessage: " + message);
        final String requestId = parts[0];
        final String type = parts[1];
        final String cmd = parts[2];
        final NodeCommandSourceStack source = new NodeCommandSourceStack(this.getServer(), this.client, requestId);
        this.handleCommand(source, type, cmd);
    }

    private void handleCommand(final NodeCommandSourceStack source, final String type, final String cmd) {
        if (type.equals("cmd")) {
            this.getServer().handleConsoleInput(cmd, source);
            return;
        }

        if (type.equals("eventSubscription")) {
            this.onEventSubscriptionCommand(source, cmd);
            return;
        }

        if (type.startsWith(MSG_PREFIX_INTEROP)) {
            this.onInteropCommand(source, type, cmd);
            return;
        }

        LOGGER.warn("Unrecognized message type: {}", type);
    }

    private void onEventSubscriptionCommand(final NodeCommandSourceStack source, final String cmd) {
        NodeInteropClientSubscriptionCommandEvent event = new NodeInteropClientSubscriptionCommandEvent(source, cmd);
        MinecraftForge.EVENT_BUS.post(event);
    }

    private void onInteropCommand(final NodeCommandSourceStack source, final String type, final String cmd) {
        NodeInteropCommandEvent event = new NodeInteropCommandEvent(this.getServer(), this.client, source, type, cmd);
        MinecraftForge.EVENT_BUS.post(event);
    }
}