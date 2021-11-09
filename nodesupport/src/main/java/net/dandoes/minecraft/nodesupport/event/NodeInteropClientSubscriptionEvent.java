package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;
import net.dandoes.minecraft.nodesupport.NodeInteropClientSubscriptionAction;
import net.minecraftforge.eventbus.api.Event;

public class NodeInteropClientSubscriptionEvent extends NodeInteropClientCommandEvent {

    private final NodeInteropClientSubscriptionAction action;
    private final String eventClassName;

    public NodeInteropClientSubscriptionEvent(
        final NodeCommandSourceStack source,
        final NodeInteropClientSubscriptionAction action,
        final String eventClassName
    ) {
        super(source);
        this.action = action;
        this.eventClassName = eventClassName;
    }

    public NodeInteropClientSubscriptionAction getAction() {
        return this.action;
    }

    public String getEventClassName() {
        return this.eventClassName;
    }

    public Class<? extends Event> getEventClass() throws ClassNotFoundException {
        final Class<?> eventClass = Class.forName(this.getEventClassName());
        return (Class<? extends Event>) eventClass;
    }
}
