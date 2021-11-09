package net.dandoes.minecraft.nodesupport;

import com.mojang.brigadier.ResultConsumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class NodeCommandSourceStack extends CommandSourceStack {

    private static final int NODE_COMMAND_PERMISSION_LEVEL = 4;

    protected final NodeCommandSource source;

    public NodeCommandSourceStack(final DedicatedServer server, final NodeInteropClient interopClient, final String requestId) {
        this(
                new NodeCommandSource(interopClient, requestId),
                Vec3.ZERO,
                Vec2.ZERO,
                server.overworld(),
                NODE_COMMAND_PERMISSION_LEVEL,
                "Server",
                new TextComponent("Server"),
                server,
                null,
                false,
                null,
                null
        );
    }

    protected NodeCommandSourceStack(
        NodeCommandSource source,
        Vec3 worldPosition,
        Vec2 rotation,
        ServerLevel level,
        int permissionLevel,
        String textName,
        Component displayName,
        MinecraftServer server,
        @Nullable Entity entity,
        boolean silent,
        ResultConsumer<CommandSourceStack> consumer,
        EntityAnchorArgument.Anchor anchor
    ) {
        super(source, worldPosition, rotation, level, permissionLevel, textName, displayName, server, entity, silent, consumer, anchor);
        this.source = source;
    }

    public NodeCommandSource getSource() {
        return this.source;
    }

    public void sendSuccess(Component message, boolean allowLogging) {
        super.sendSuccess(message, allowLogging);
    }

    public void sendErrorMessage(final Exception ex) {
        this.source.sendResponse(ex);
    }

    public NodeInteropClient getInteropClient() {
        return this.source.getInteropClient();
    }

}
