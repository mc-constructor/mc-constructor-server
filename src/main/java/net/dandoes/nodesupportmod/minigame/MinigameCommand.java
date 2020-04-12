package net.dandoes.nodesupportmod.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dandoes.nodesupportmod.NodeInteropChannelHandler;
import net.dandoes.nodesupportmod.minigame.event.MinigameEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class MinigameCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> b = Commands.literal("minigame")
            .requires((player) -> player.hasPermissionLevel(1));

        b.then(
            Commands.literal("start").then(
                Commands.argument("minigame", MinigameArgument.minigames())
                    .suggests(MinigameArgument.SUGGEST_MINIGAMES)
                    .executes(context -> {
                        NodeInteropChannelHandler.checkHasHandler();
                        return startGame(context, MinigameArgument.getMinigame(context, "minigame"));
                    })
            )
        );

        dispatcher.register(b);
    }

    public static int startGame(CommandContext<CommandSource> context, Minigame game) {
        MinigameEvent event = new MinigameEvent.MinigameStartEvent(game);
        final int result = NodeInteropChannelHandler.sendToAllHandlers(event);
        context.getSource().sendFeedback(event.getAction(), true);
        return result;
    }

}
