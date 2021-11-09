package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;

public class NodeInteropClientCommandEvent extends NodeInteropClientEvent {

    private final NodeCommandSourceStack source;

    public NodeInteropClientCommandEvent(final NodeCommandSourceStack source) {
        super(source.getInteropClient());
        this.source = source;
    }

    public NodeCommandSourceStack getSource() {
        return this.source;
    }
}
