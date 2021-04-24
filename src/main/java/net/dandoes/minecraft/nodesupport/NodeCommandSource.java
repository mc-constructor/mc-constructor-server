package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NodeCommandSource extends CommandSource {
    private static final Logger LOGGER = LogManager.getLogger(NodeCommandSource.class);

    private final NodeInteropServer interopServer;
    private final NodeInteropClient interopClient;
    private final String requestId;

    public NodeCommandSource(final NodeInteropServer interopServer, final NodeInteropClient interopClient, final String requestId) {
        this(interopServer, interopServer.getServer(), interopClient, requestId);
    }

    protected NodeCommandSource(final NodeInteropServer interopServer, final DedicatedServer server, final NodeInteropClient interopClient, final String requestId) {
        super(
            server,
            server.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(interopServer.getServer().getWorld(DimensionType.OVERWORLD).getSpawnPoint()),
            Vec2f.ZERO,
            server.getWorld(DimensionType.OVERWORLD),
            4,
            "Server",
            new StringTextComponent("Server"),
            server,
            null
        );
        this.interopServer = interopServer;
        this.interopClient = interopClient;
        this.requestId = requestId;
    }

    public NodeInteropServer getInteropServer() {
        return this.interopServer;
    }

    public NodeInteropClient getInteropClient() {
        return interopClient;
    }

    public String getRequestId() {
        return this.requestId;
    }

    @Override
    public void sendFeedback(ITextComponent message, boolean allowLogging) {
        LOGGER.info("sending feedback for requestId " + this.requestId);
        this.interopClient.sendResponse(this, message);
    }

    @Override
    public void sendErrorMessage(ITextComponent message) {
        LOGGER.info("sending error message for requestId " + this.requestId + ": " + message.getUnformattedComponentText());
        final SimpleCommandExceptionType exceptionType = new SimpleCommandExceptionType(message);
        this.interopClient.sendResponse(this, exceptionType.create());
    }

    public void sendErrorMessage(Exception ex) {
        LOGGER.info("sending error message for requestId " + this.requestId + ": " + ex.getLocalizedMessage());
        this.interopClient.sendResponse(this, ex);
    }
}
