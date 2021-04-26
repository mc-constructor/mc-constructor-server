package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSource;
import net.dandoes.minecraft.nodesupport.NodeInteropClient;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.eventbus.api.Event;

public class NodeInteropCommandEvent extends Event {

    private final DedicatedServer server;
    private final NodeInteropClient client;
    private final NodeCommandSource source;
    private final String type;
    private final String cmd;

    public NodeInteropCommandEvent(final DedicatedServer server, final NodeInteropClient client, final NodeCommandSource source, final String type, final String cmd) {
        this.server = server;
        this.client = client;
        this.source = source;
        this.type = type;
        this.cmd = cmd;
    }

    public DedicatedServer getServer() {
        return server;
    }

    public NodeInteropClient getClient() {
        return client;
    }

    public NodeCommandSource getSource() {
        return source;
    }

    public String getCmd() {
        return cmd;
    }

    public String getType() {
        return type;
    }

}
