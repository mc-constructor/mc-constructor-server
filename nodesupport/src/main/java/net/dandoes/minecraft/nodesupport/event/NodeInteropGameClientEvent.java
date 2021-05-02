package net.dandoes.minecraft.nodesupport.event;

import net.minecraftforge.eventbus.api.Event;

import java.util.Collection;

public abstract class NodeInteropGameClientEvent extends Event {

    public abstract Collection<String> getInteropResponseContent();

}
