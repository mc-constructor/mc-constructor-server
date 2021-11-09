package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.serialization.events.EventData;
import net.minecraftforge.eventbus.api.Event;

public abstract class NodeInteropGameClientEvent extends Event {

    public abstract <TEvent extends NodeInteropGameClientEvent> EventData<TEvent> getInteropEventData();

}
