package net.dandoes.minecraft.nodesupport;

import net.dandoes.minecraft.nodesupport.event.NodeInteropGameClientEvent;
import net.dandoes.minecraft.nodesupport.serialization.events.EntityEventData;
import net.dandoes.minecraft.nodesupport.serialization.events.EventData;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

public class NodeInteropEventData {

    public static EventData<?> getEventData(final Event event) {
        if (event instanceof EntityEvent) {
            return EntityEventData.forEvent((EntityEvent) event);
        }
        if (event instanceof NodeInteropGameClientEvent) {
            final NodeInteropGameClientEvent nodeInteropGameClientEvent = (NodeInteropGameClientEvent) event;
            return nodeInteropGameClientEvent.getInteropEventData();
        }
        return null;
    }
}
