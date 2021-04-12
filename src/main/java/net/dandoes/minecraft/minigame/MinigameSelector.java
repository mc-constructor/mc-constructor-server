package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;

public class MinigameSelector {

    private final String key;

    public MinigameSelector(final String key) {
        this.key = key;
    }

    public Minigame getGame(CommandSource source) throws CommandSyntaxException {
        Minigame game = MinigameManager.getGame(this.key);
        if (game == null) {
            throw MinigameArgument.NOT_FOUND.create();
        }

        return game;
    }
}
