package net.dandoes.minecraft.minigame.event;

import java.util.Arrays;
import java.util.Collection;

import net.dandoes.minecraft.minigame.Minigame;
import net.dandoes.minecraft.nodesupport.event.NodeInteropGameClientEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MinigameGameClientEvent extends NodeInteropGameClientEvent {

    private final Minigame game;
    private final ITextComponent action;

    private MinigameGameClientEvent(final Minigame game, final String actionKey) {
        this.game = game;
        this.action = new TranslationTextComponent("event.minigame.start", game);
    }

    public Minigame getGame() {
        return game;
    }

    public ITextComponent getAction() {
        return action;
    }

    @Override()
    public Collection<String> getInteropResponseContent() {
        return Arrays.asList(
            this.getAction().getString(),
            this.getGame().getKey()
        );
    }

    public static class MinigameStartGameClientEvent extends MinigameGameClientEvent {
        public MinigameStartGameClientEvent(final Minigame game) {
            super(game, "event.minigame.start");
        }
    }

    public static class MinigameStopGameClientEvent extends MinigameGameClientEvent {
        public MinigameStopGameClientEvent(final Minigame game) {
            super(game, "event.minigame.stop");
        }
    }

    public static class MinigameResetGameClientEvent extends MinigameGameClientEvent {
        public MinigameResetGameClientEvent(final Minigame game) {
            super(game, "event.minigame.reset");
        }
    }

}
