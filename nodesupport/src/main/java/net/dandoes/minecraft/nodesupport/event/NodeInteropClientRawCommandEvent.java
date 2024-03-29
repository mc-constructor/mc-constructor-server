package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSource;

public class NodeInteropClientRawCommandEvent extends NodeInteropClientCommandEvent {

    private final String cmd;

    public NodeInteropClientRawCommandEvent(final NodeCommandSource source, final String cmd) {
        super(source);
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }
}
