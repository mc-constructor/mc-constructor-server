package net.dandoes.minecraft.nodesupport;

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
