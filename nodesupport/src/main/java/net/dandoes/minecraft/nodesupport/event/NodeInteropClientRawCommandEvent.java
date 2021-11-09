package net.dandoes.minecraft.nodesupport.event;

import net.dandoes.minecraft.nodesupport.NodeCommandSourceStack;

public class NodeInteropClientRawCommandEvent extends NodeInteropClientCommandEvent {

    private final String cmd;

    public NodeInteropClientRawCommandEvent(final NodeCommandSourceStack source, final String cmd) {
        super(source);
        this.cmd = cmd;
    }

    public String getCmd() {
        return this.cmd;
    }
}
