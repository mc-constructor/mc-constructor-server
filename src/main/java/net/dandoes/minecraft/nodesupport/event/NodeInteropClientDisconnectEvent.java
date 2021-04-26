package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeInteropClient;

public class NodeInteropClientDisconnectEvent extends NodeInteropClientEvent {
    public NodeInteropClientDisconnectEvent(final NodeInteropClient interopClient) {
        super(interopClient);
    }
}
