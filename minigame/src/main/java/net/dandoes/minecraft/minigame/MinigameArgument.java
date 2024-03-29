package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class MinigameArgument implements ArgumentType<MinigameSelector> {
    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(new TranslationTextComponent("argument.minigame.notfound"));

    // TODO: figure out why suggestions aren't making it to the client
    public static final SuggestionProvider<CommandSource> SUGGEST_MINIGAMES = (context, builder) -> {
        final StringReader stringreader = new StringReader(context.getInput());
        stringreader.setCursor(builder.getStart());
        for (final Minigame game : MinigameManager.getGames()) {
            builder.suggest(game.getKey(), game.getTitle());
        }
        return builder.buildFuture();
    };

    public static Minigame getMinigame(final CommandContext<CommandSource> context, final String name) throws CommandSyntaxException {
        return context.getArgument(name, MinigameSelector.class).getGame(context.getSource());
    }

    public static MinigameArgument minigameArgument() {
        return new MinigameArgument();
    }

    @Override
    public MinigameSelector parse(final StringReader reader) throws CommandSyntaxException {
        return new MinigameSelector(StringArgumentType.string().parse(reader));
    }

}
