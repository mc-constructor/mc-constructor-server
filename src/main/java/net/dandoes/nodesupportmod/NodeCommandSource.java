package net.dandoes.nodesupportmod;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

public class NodeCommandSource extends CommandSource {

    public final String requestId;
    private final NodeClient client;

    public NodeCommandSource(MinecraftServer server, String requestId, NodeClient client) {
        super(server, server.getWorld(DimensionType.OVERWORLD) == null ? Vec3d.ZERO : new Vec3d(server.getWorld(DimensionType.OVERWORLD).getSpawnPoint()), Vec2f.ZERO, server.getWorld(DimensionType.OVERWORLD), 4, "Server", new StringTextComponent("Server"), server, (Entity)null);
        this.requestId = requestId;
        this.client = client;
    }

    @Override
    public void sendFeedback(ITextComponent message, boolean allowLogging) {
        super.sendFeedback(message, allowLogging);
        this.client.sendResponse(this, message);
    }
}
