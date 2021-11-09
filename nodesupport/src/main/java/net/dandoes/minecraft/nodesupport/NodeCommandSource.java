package net.dandoes.minecraft.nodesupport;

import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class NodeCommandSource implements CommandSource {
    private static final Logger LOGGER = LogManager.getLogger(NodeCommandSource.class);

    private final NodeInteropClient interopClient;
    private final String requestId;

    public NodeCommandSource(final NodeInteropClient interopClient, final String requestId) {
        this.interopClient = interopClient;
        this.requestId = requestId;
    }

    public NodeInteropClient getInteropClient() {
        return interopClient;
    }

    public String getRequestId() {
        return this.requestId;
    }

    @Override
    public void sendMessage(Component msg, UUID p_80167_) {
        LOGGER.info("sending success for requestId " + this.requestId);
        this.sendResponse(msg);
    }

    @Override
    public boolean acceptsSuccess() {
        return true;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }

    public void sendResponse(final Component msg) {
        LOGGER.info("sending message for requestId " + this.getRequestId() + ": " + msg.getString());
        this.interopClient.sendResponse(this, msg);
    }

    public void sendResponse(final Exception ex) {
        LOGGER.info("sending exception message for requestId " + this.getRequestId() + ": " + ex.getLocalizedMessage());
        this.interopClient.sendResponse(this, ex);
    }
}
