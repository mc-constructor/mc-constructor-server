package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;

public class NodeInteropClientSubscriptionCommandEvent extends NodeInteropClientRawCommandEvent {
    public NodeInteropClientSubscriptionCommandEvent(final NodeCommandSourceStack source, final String cmd) {
        super(source, cmd);
    }
}
