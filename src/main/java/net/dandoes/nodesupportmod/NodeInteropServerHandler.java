package net.dandoes.nodesupportmod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import net.dandoes.nodesupportmod.minigame.MinigameManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ChannelHandler.Sharable
public class NodeInteropServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropServerHandler.class);

    private final SocketChannel channel;
    private final MinecraftServer server;
    private StringBuilder buffer = new StringBuilder();
    private boolean isReady = false;
    private final char newline = "\n".charAt(0);
    private final Lock bufferLock = new ReentrantLock();
    private NodeClient client;
    private String delimiter;

    public NodeInteropServerHandler(SocketChannel channel, MinecraftServer server) {
        this.channel = channel;
        this.server = server;
        LOGGER.debug("new NodeInteropServerHandler");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            this.bufferLock.lock();
            while (in.isReadable()) {
                char c = (char) in.readByte();
                if (!this.isReady && c == this.newline) {
                    this.delimiter = this.buffer.toString();
                    this.isReady = true;
                    this.buffer.delete(0, this.buffer.length());
                    LOGGER.debug("got delimiter: " + this.delimiter);
                    try {
                        ChannelWriter writer = new ChannelWriter(this.channel, this.delimiter);
                        this.client = new NodeClient(writer);
                        this.channel.writeAndFlush(Unpooled.copiedBuffer(this.delimiter, CharsetUtil.UTF_8)).sync();
                    } catch (Exception ex) {
                        LOGGER.error(ex);
                    }
                    continue;
                }
                this.buffer.append(c);
            }
            LOGGER.debug("done reading channel: " + this.buffer.toString());
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

    public void sendEvent(Event event) {
        if (this.client != null) {
            this.client.sendEvent(event);
        }
    }

    private void checkBuffer() throws Exception {
        int endMessageIndex = this.buffer.indexOf(this.delimiter);
        if (endMessageIndex < 0) {
            return;
        }
        List<String> messages = this.getNextFromBuffer();
        for (String message : messages) {
            this.handleMessage(message);
        }
    }

    private List<String> getNextFromBuffer() {
        List<String> messages = new ArrayList<>();
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

    private void handleMessage(String message) throws Exception {
        String[] parts = message.split("\n");
        if (parts.length > 3) {
            throw new Exception("Unexpected message length");
        }
        LOGGER.debug("handleIncomingMessage: " + message);
        String requestId = parts[0];
        String type = parts[1];
        String cmd = parts[2];
        NodeCommandSource source = new NodeCommandSource(server, requestId, client);
        this.handleCommand(source, type, cmd);
    }

    private void handleCommand(NodeCommandSource source, String type, String cmd) throws Exception {
        if (type.equals("cmd")) {
            if (server instanceof DedicatedServer) {
                DedicatedServer dServer = (DedicatedServer) server;
                dServer.handleConsoleInput(cmd, source);
            } else {
                throw new Exception("wha huh?");
            }
        }
        if (type.equals("minigame")) {
            MinigameManager.handleMessage(source, cmd);
        }
    }
}