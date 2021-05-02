package net.dandoes.minecraft.nodesupport;

import io.netty.buffer.Unpooled;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelWriter {
    private final SocketChannel channel;
    private final String delimiter;
    private final Lock channelWriteLock = new ReentrantLock();
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    public ChannelWriter(final SocketChannel channel, final String delimiter) {
        this.delimiter = delimiter;
        this.channel = channel;
    }

    public void writeMessage(final String message) {
        this.queue.add(message);
        CompletableFuture.runAsync(this::flush);
    }

    private void flush() {
        if (this.queue.isEmpty()) {
            return;
        }

        try {
            this.channelWriteLock.lock();
            if (this.queue.isEmpty()) {
                return;
            }
            while (this.queue.peek() != null) {
                final String message = this.queue.poll();
                if (message == null) {
                    break;
                }

                this.channel.write(Unpooled.copiedBuffer(message + this.delimiter, CharsetUtil.UTF_8));
            }
            this.channel.flush();
        } finally {
            this.channelWriteLock.unlock();
        }
    }
}
