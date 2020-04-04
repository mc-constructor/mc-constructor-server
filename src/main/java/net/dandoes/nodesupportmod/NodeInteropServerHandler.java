package net.dandoes.nodesupportmod;

import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NodeInteropServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LogManager.getLogger();

    private final SocketChannel channel;
    private final MinecraftServer server;
    private StringBuilder buffer = new StringBuilder();
    private boolean isReady = false;
    private final char newline = "\n".charAt(0);
    private final Lock bufferLock = new ReentrantLock();
    private String delimiter;
    private NodeClient client;

    public NodeInteropServerHandler(SocketChannel channel, MinecraftServer server) {
        this.channel = channel;
        this.server = server;
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
                    LOGGER.info("got delimiter: " + this.delimiter);
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
        this.client.sendEvent(event);
    }

    private void checkBuffer() throws Exception {
        System.out.println("Checking buffer: " + this.buffer.toString());
        int endMessageIndex = this.buffer.indexOf(this.delimiter);
        if (endMessageIndex < 0) {
            return;
        }
        String message = this.getNextFromBuffer();
        if (message != null) {
            this.handleIncomingMessage(message);
        }
    }

    private String getNextFromBuffer() {
        String message;
        try {
            this.bufferLock.lock();
            int endMessageIndex = this.buffer.indexOf(this.delimiter);
            if (endMessageIndex < 0) {
                return null;
            }
            message = this.buffer.substring(0, endMessageIndex);
            this.buffer.delete(0, endMessageIndex + this.delimiter.length());
            return message;
        }
        finally {
            this.bufferLock.unlock();
        }
    }

    private void handleIncomingMessage(String message) throws Exception {
        String[] parts = message.split("\n");
        if (parts.length > 3) {
            throw new Exception("Unexpected message length");
        }
        String requestId = parts[0];
        String type = parts[1];
        String cmd = parts[2];
        if (type.equals("cmd")) {
            CommandDispatcher<CommandSource> dispatcher = this.server.getCommandManager().getDispatcher();
            NodeCommandSource source = new NodeCommandSource(this.server, requestId, this.client);
            try {
                dispatcher.execute(cmd, source);
            } catch (Exception ex) {
                this.client.sendResponse(source, ex);
            }
        }
    }
}