package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSource;

public class NodeInteropClientSubscriptionCommandEvent extends NodeInteropClientRawCommandEvent {
    public NodeInteropClientSubscriptionCommandEvent(final NodeCommandSource source, final String cmd) {
        super(source, cmd);
    }
}
