package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dandoes.minecraft.nodesupport.NodeCommandDispatcher;
import net.dandoes.minecraft.nodesupport.NodeCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MinigameManager {

    private static final NodeCommandDispatcher dispatcher = new NodeCommandDispatcher();
    private static final Map<String, Minigame> games = new HashMap<>();

    static {
        MinigameManagerCommand.register((CommandDispatcher<CommandSource>) (Object) dispatcher);
    }

    public static void handleMessage(NodeCommandSource source, String cmd) {
        try {
            dispatcher.execute(cmd, source);
        } catch (CommandSyntaxException ex) {
            source.sendErrorMessage(ex);
        }
    }

    public static Minigame registerGame(String key, ITextComponent title, ITextComponent description) {
        Minigame game = new Minigame(key, title, description);
        games.put(key, game);
        return game;
    }

    public static void unregisterGame(Minigame game) {
        games.remove(game.getKey());
    }

    public static Minigame getGame(final String key) {
        return games.get(key);
    }

    public static Collection<Minigame> getGames() {
        return games.values();
    }

    public static Collection<String> getGameKeys() {
        return games.keySet();
    }

    public static void checkHasGame(String key) throws CommandSyntaxException {
        if (!games.containsKey(key)) {
            throw MinigameArgument.NOT_FOUND.create();
        }
    }

}
