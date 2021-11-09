package net.dandoes.minecraft.nodesupport.serialization.events;

import net.dandoes.minecraft.nodesupport.serialization.MessageData;
import net.minecraftforge.eventbus.api.Event;

public class EventData<TEvent extends Event> extends MessageData<TEvent> {
    public final String name;

    public EventData(final TEvent event) {
        super(event);
        this.name = event.getClass().getName();
    }

    protected TEvent getEvent() {
        return this.getDataSource();
    }
}
