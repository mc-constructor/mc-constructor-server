package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dandoes.minecraft.minigame.event.MinigameGameClientEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.common.MinecraftForge;

public class MinigameCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> b = Commands.literal("minigame")
            .requires((player) -> player.hasPermissionLevel(1));

        b.then(
            Commands.literal("start").then(
                Commands.argument("minigame", MinigameArgument.minigames())
                    .suggests(MinigameArgument.SUGGEST_MINIGAMES)
                    .executes(context -> startGame(context, MinigameArgument.getMinigame(context, "minigame")))
            )
        );

        dispatcher.register(b);
    }

    public static int startGame(CommandContext<CommandSource> context, Minigame game) {
        MinigameGameClientEvent event = new MinigameGameClientEvent.MinigameStartGameClientEvent(game);
        MinecraftForge.EVENT_BUS.post(event);
        context.getSource().sendFeedback(event.getAction(), true);
        return Command.SINGLE_SUCCESS;
    }

}
