package net.dandoes.minecraft.nodesupport;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelWriter {
    private final SocketChannel channel;
    private final Lock channelWriteLock = new ReentrantLock();
    private final String delimiter;

    public ChannelWriter(SocketChannel channel, String delimiter) {
        this.delimiter = delimiter;
        this.channel = channel;
    }

    public void writeMessage(final String message) {
        try {
            this.channelWriteLock.lock();
            this.channel.writeAndFlush(Unpooled.copiedBuffer(message + this.delimiter, CharsetUtil.UTF_8));
        }
        finally {
            this.channelWriteLock.unlock();
        }
    }
}
