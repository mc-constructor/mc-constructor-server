package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.dandoes.minecraft.minigame.event.MinigameGameClientEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;

@FunctionalInterface
interface MinigameEventFn {
    MinigameGameClientEvent run(final Minigame minigame);
}

public class MinigameCommand {

    private static LiteralArgumentBuilder<CommandSource> minigameGameCommand(
        final String name,
        final MinigameEventFn createEvent
    ) {
        return Commands.literal(name)
                .then(Commands.argument("minigame", MinigameArgument.minigameArgument())
                    .suggests(MinigameArgument.SUGGEST_MINIGAMES)
                    .executes(context -> {
                        final Minigame minigame = MinigameArgument.getMinigame(context, "minigame");
                        final MinigameGameClientEvent event = createEvent.run(minigame);
                        MinecraftForge.EVENT_BUS.post(event);
                        context.getSource().sendSuccess(event.getAction(), true);
                        return Command.SINGLE_SUCCESS;
                    })
                );
    }

    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> b = Commands.literal("minigame")
            .requires((player) -> player.hasPermission(1));

        b.then(minigameGameCommand("start", MinigameGameClientEvent.MinigameStartGameClientEvent::new));
        b.then(minigameGameCommand("stop", MinigameGameClientEvent.MinigameStopGameClientEvent::new));
        b.then(minigameGameCommand("reset", MinigameGameClientEvent.MinigameResetGameClientEvent::new));

        b.then(Commands.literal("list").executes(MinigameCommand::listGames));

        dispatcher.register(b);
    }

    public static int listGames(final CommandContext<CommandSource> context) {
        final CommandSource source = context.getSource();
        final Collection<Minigame> games = MinigameManager.getGames();
        if (games.isEmpty()) {
            source.sendSuccess(new TranslationTextComponent("event.minigame.list.empty"), true);
            return Command.SINGLE_SUCCESS;
        }
        for (final Minigame game : MinigameManager.getGames()) {
           MinigameCommand.listGame(source, game);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void listGame(final CommandSource source, final Minigame game) {
        final ITextComponent gameTitleText = new TranslationTextComponent(
            "event.minigame.list.game.title",
            game.getTitle(),
            game.getKey()
        );
        source.sendSuccess(gameTitleText, true);

        final ITextComponent description = game.getDescription();
        if (description == null) {
            return;
        }

        final ITextComponent gameDescriptionText = new TranslationTextComponent(
                "event.minigame.list.game.description",
                description);
        source.sendSuccess(gameDescriptionText, true);
    }

}
