package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dandoes.minecraft.minigame.event.MinigameGameClientEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;

@FunctionalInterface
interface MinigameEventFn {
    MinigameGameClientEvent run(final Minigame minigame);
}

public class MinigameCommand {

    private static LiteralArgumentBuilder<CommandSourceStack> minigameGameCommand(
        final String name,
        final MinigameEventFn createEvent
    ) {
        return Commands.literal(name)
                .then(Commands.argument("minigame", MinigameArgumentType.minigameName())
                    .suggests(MinigameArgumentType.SUGGEST_MINIGAME_NAMES)
                    .executes(context -> {
                        final Minigame minigame = MinigameArgumentType.getMinigame(context, "minigame");
                        final MinigameGameClientEvent event = createEvent.run(minigame);
                        MinecraftForge.EVENT_BUS.post(event);
                        context.getSource().sendSuccess(event.getAction(), true);
                        return Command.SINGLE_SUCCESS;
                    })
                );
    }

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        final LiteralArgumentBuilder<CommandSourceStack> b = Commands.literal("minigame")
            .requires((player) -> player.hasPermission(1));

        b.then(minigameGameCommand("start", MinigameGameClientEvent.MinigameStartGameClientEvent::new));
        b.then(minigameGameCommand("stop", MinigameGameClientEvent.MinigameStopGameClientEvent::new));
        b.then(minigameGameCommand("reset", MinigameGameClientEvent.MinigameResetGameClientEvent::new));

        b.then(Commands.literal("list").executes(MinigameCommand::listGames));

        dispatcher.register(b);
    }

    public static int listGames(final CommandContext<CommandSourceStack> context) {
        final CommandSourceStack source = context.getSource();
        final Collection<Minigame> games = MinigameManager.getGames();
        if (games.isEmpty()) {
            source.sendSuccess(new TranslatableComponent("net.dandoes.minecraft.minigame.event.minigame.list.empty"), true);
            return Command.SINGLE_SUCCESS;
        }
        for (final Minigame game : MinigameManager.getGames()) {
           MinigameCommand.listGame(source, game);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void listGame(final CommandSourceStack source, final Minigame game) {
        final Component gameTitleText = new TranslatableComponent(
            "net.dandoes.minecraft.minigame.event.minigame.list.game.title",
            game.getTitle(),
            game.getKey()
        );
        source.sendSuccess(gameTitleText, true);

        final Component description = game.getDescription();
        if (description == null) {
            return;
        }

        final Component gameDescriptionText = new TranslatableComponent(
                "net.dandoes.minecraft.minigame.event.minigame.list.game.description",
                description);
        source.sendSuccess(gameDescriptionText, true);
    }

}
