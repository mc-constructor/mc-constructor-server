package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSource;

public class NodeInteropClientCommandEvent extends NodeInteropClientEvent {

    private final NodeCommandSource source;

    public NodeInteropClientCommandEvent(final NodeCommandSource source) {
        super(source.getInteropClient());
        this.source = source;
    }

    public NodeCommandSource getSource() {
        return this.source;
    }
}
