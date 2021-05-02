package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dandoes.minecraft.nodesupport.NodeCommandSource;
import net.dandoes.minecraft.nodesupport.NodeInteropClient;
import net.dandoes.minecraft.nodesupport.event.NodeInteropCommandEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

public class MinigameManager {

    private static final Map<String, Minigame> games = new HashMap<>();
    private static final Map<NodeInteropClient, Set<String>> interopClientGames = new HashMap<>();

    public static void handleCommand(final NodeInteropCommandEvent event) {
        final NodeCommandSource source = event.getSource();
        final CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommands().getDispatcher();
        try {
            dispatcher.execute(event.getCmd(), source);
        } catch (CommandSyntaxException ex) {
            source.sendErrorMessage(ex);
        }
    }

    public static Minigame registerGame(final NodeInteropClient interopClient, String key, final ITextComponent title, final ITextComponent description) throws MinigameRegistrationKeyConflictException {
        final Minigame game = new Minigame(key, title, description);

        if (games.containsKey(key)) {
            throw new MinigameRegistrationKeyConflictException();
        }

        games.put(key, game);
        if (!interopClientGames.containsKey(interopClient)) {
            interopClientGames.put(interopClient, new HashSet<>());
        }
        interopClientGames.get(interopClient).add(key);
        return game;
    }

    public static void unregisterGame(final NodeInteropClient interopClient, final Minigame game) {
        games.remove(game.getKey());
        if (interopClientGames.containsKey(interopClient)) {
            interopClientGames.get(interopClient).remove(game.getKey());
        }
    }

    public static void removeInteropClient(final NodeInteropClient interopClient) {
        if (!interopClientGames.containsKey(interopClient)) {
            return;
        }
        interopClientGames.get(interopClient).forEach(key -> unregisterGame(interopClient, games.get(key)));
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

    public static void checkHasGame(final String key) throws CommandSyntaxException {
        if (!games.containsKey(key)) {
            throw MinigameArgument.NOT_FOUND.create();
        }
    }

}
