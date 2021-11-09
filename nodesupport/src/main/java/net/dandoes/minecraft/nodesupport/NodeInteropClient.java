package net.dandoes.minecraft.nodesupport;

import net.dandoes.minecraft.nodesupport.serialization.CommandResponseData;
import net.dandoes.minecraft.nodesupport.serialization.MessageData;
import net.dandoes.minecraft.nodesupport.serialization.events.EventData;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;

public class NodeInteropClient {
    private static final Logger LOGGER = LogManager.getLogger(NodeInteropClient.class);

    private final ChannelWriter writer;

    private final Map<Class<? extends Event>, Consumer<? extends Event>> subscribedEvents = new HashMap<>();

    public NodeInteropClient(final ChannelWriter writer) {
        this.writer = writer;
    }

    public void sendEvent(final Event event) {
        final EventData<?> eventData = NodeInteropEventData.getEventData(event);
        LOGGER.debug("sendEvent: {}", eventData);
        this.writer.writeMessage(eventData);
    }

    public void sendResponse(final NodeCommandSource source, final Component message) {
        final MessageData<?> messageData = new CommandResponseData.CommandSuccessResponseData(source, message);
        LOGGER.debug("sendResponse: {}", messageData);
        this.writer.writeMessage(messageData);
    }
    public void sendResponse(final NodeCommandSource source, final Exception ex) {
        final MessageData<?> messageData = new CommandResponseData.CommandExceptionResponseData(source, ex);
        LOGGER.debug("sendResponse: {}", messageData);
        this.writer.writeMessage(messageData);
    }

    public <C extends Class<T>, T extends Event> void subscribeToEvent(final C eventClass) {
        if (!this.subscribedEvents.containsKey(eventClass)) {
            final Consumer<T> consumer = this::sendEvent;
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventClass, consumer);
            this.subscribedEvents.put(eventClass, consumer);
        }
    }

    public void unsubscribeFromEvent(final Class<? extends Event> eventClass) {
        if (this.subscribedEvents.containsKey(eventClass)) {
            final Consumer<? extends Event> consumer = this.subscribedEvents.get(eventClass);
            MinecraftForge.EVENT_BUS.unregister(consumer);
            this.subscribedEvents.remove(eventClass);
        }
    }
}
