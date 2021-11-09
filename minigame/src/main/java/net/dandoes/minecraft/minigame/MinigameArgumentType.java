package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MinigameArgumentType implements ArgumentType<MinigameSelector> {
    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("net.dandoes.minecraft.minigame.argument.minigame.notfound"));

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MINIGAME_NAMES = (context, builder) -> {
        final StringReader stringreader = new StringReader(context.getInput());
        stringreader.setCursor(builder.getStart());
        for (final Minigame game : MinigameManager.getGames()) {
            builder.suggest(game.getKey(), game.getTitle());
        }
        return builder.buildFuture();
    };

    public static Minigame getMinigame(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
        return context.getArgument(name, MinigameSelector.class).getGame(context.getSource());
    }

    public static MinigameArgumentType minigameName() {
        return new MinigameArgumentType(MinigameType.MINIGAME_NAME);
    }

    private final MinigameArgumentType.MinigameType type;

    public MinigameArgumentType(final MinigameArgumentType.MinigameType type) {
        this.type = type;
    }

    public MinigameType getType() {
        return this.type;
    }

    @Override
    public MinigameSelector parse(final StringReader reader) throws CommandSyntaxException {
        return new MinigameSelector(StringArgumentType.word().parse(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final StringReader stringreader = new StringReader(context.getInput());
        stringreader.setCursor(builder.getStart());
        for (final Minigame game : MinigameManager.getGames()) {
            builder.suggest(game.getKey(), game.getTitle());
        }
        return builder.buildFuture();
    }

    public enum MinigameType {
        MINIGAME_NAME("word", "words_with_underscores");

        private final Collection<String> examples;

        MinigameType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        public Collection<String> getExamples() {
            return examples;
        }
    }
}
