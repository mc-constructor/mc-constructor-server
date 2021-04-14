package net.dandoes.minecraft.nodesupport;

public class NodeInteropClientEvent extends NodeInteropEvent {

    private final NodeInteropClient interopClient;

    NodeInteropClientEvent(final NodeInteropClient interopClient) {
        this.interopClient = interopClient;
    }

    public NodeInteropClient getInteropClient() {
        return this.interopClient;
    }
}
