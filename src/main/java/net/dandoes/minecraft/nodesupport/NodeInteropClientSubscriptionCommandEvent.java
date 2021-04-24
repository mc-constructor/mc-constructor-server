package net.dandoes.minecraft.nodesupport;

public class NodeInteropClientSubscriptionCommandEvent extends NodeInteropClientRawCommandEvent {
    public NodeInteropClientSubscriptionCommandEvent(NodeCommandSource source, String cmd) {
        super(source, cmd);
    }
}
