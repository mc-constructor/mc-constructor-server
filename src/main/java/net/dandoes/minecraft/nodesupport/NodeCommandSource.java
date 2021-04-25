package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodeCommandSource extends CommandSource {
    private static final Logger LOGGER = LogManager.getLogger(NodeCommandSource.class);
    private static final int NODE_COMMAND_PERMISSION_LEVEL = 4;

    private final NodeInteropClient interopClient;
    private final String requestId;

    public NodeCommandSource(final DedicatedServer server, final NodeInteropClient interopClient, final String requestId) {
        super(
            server,
            Vector3d.ZERO,
            Vector2f.ZERO,
            server.overworld(),
            NODE_COMMAND_PERMISSION_LEVEL,
            "Server",
            new StringTextComponent("Server"),
            server,
            null
        );
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
    public void sendSuccess(ITextComponent message, boolean allowLogging) {
        LOGGER.info("sending success for requestId " + this.requestId);
        this.interopClient.sendResponse(this, message);
    }

    @Override
    public void sendFailure(ITextComponent message) {
        LOGGER.info("sending failure message for requestId " + this.requestId + ": " + message.getContents());
        final SimpleCommandExceptionType exceptionType = new SimpleCommandExceptionType(message);
        this.interopClient.sendResponse(this, exceptionType.create());
    }

    public void sendErrorMessage(Exception ex) {
        LOGGER.info("sending error message for requestId " + this.requestId + ": " + ex.getLocalizedMessage());
        this.interopClient.sendResponse(this, ex);
    }
}
