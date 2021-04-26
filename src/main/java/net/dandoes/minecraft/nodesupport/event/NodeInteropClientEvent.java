package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeInteropClient;

public class NodeInteropClientEvent extends NodeInteropEvent {

    private final NodeInteropClient interopClient;

    NodeInteropClientEvent(final NodeInteropClient interopClient) {
        super();
        this.interopClient = interopClient;
    }

    public NodeInteropClient getInteropClient() {
        return this.interopClient;
    }
}
