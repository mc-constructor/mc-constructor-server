package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;

public class MinigameSelector {

    private final String key;

    public MinigameSelector(final String key) {
        this.key = key;
    }

    public Minigame getGame(final CommandSourceStack source) throws CommandSyntaxException {
        final Minigame game = MinigameManager.getGame(this.key);
        if (game == null) {
            throw MinigameArgumentType.NOT_FOUND.create();
        }

        return game;
    }
}
