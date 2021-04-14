package net.dandoes.minecraft.nodesupport;

import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NodeInteropEventsRegistry {

    private final NodeInteropServer interopServer;

    NodeInteropEventsRegistry(final NodeInteropServer interopServer) {
        this.interopServer = interopServer;
    }

    @SubscribeEvent
    public void onInteropEvent(final NodeInteropGameClientEvent event) {
        this.interopServer.broadcast(event);
    }
}
