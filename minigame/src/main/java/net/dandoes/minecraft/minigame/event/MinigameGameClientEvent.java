package net.dandoes.minecraft.minigame.event;

import net.dandoes.minecraft.minigame.Minigame;
import net.dandoes.minecraft.minigame.serialization.MinigameGameClientEventData;
import net.dandoes.minecraft.nodesupport.event.NodeInteropGameClientEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class MinigameGameClientEvent extends NodeInteropGameClientEvent {

    private final Minigame game;
    private final Component action;

    private MinigameGameClientEvent(final Minigame game, final String actionKey) {
        this.game = game;
        this.action = new TranslatableComponent(actionKey, game.getTitle());
    }

    public Minigame getGame() {
        return game;
    }

    public Component getAction() {
        return action;
    }

    @Override()
    public MinigameGameClientEventData<?> getInteropEventData() {
        return new MinigameGameClientEventData<>(this);
    }

    public static class MinigameStartGameClientEvent extends MinigameGameClientEvent {
        public MinigameStartGameClientEvent(final Minigame game) {
            super(game, "net.dandoes.minecraft.minigame.event.minigame.start");
        }
    }

    public static class MinigameStopGameClientEvent extends MinigameGameClientEvent {
        public MinigameStopGameClientEvent(final Minigame game) {
            super(game, "net.dandoes.minecraft.minigame.event.minigame.stop");
        }
    }

    public static class MinigameResetGameClientEvent extends MinigameGameClientEvent {
        public MinigameResetGameClientEvent(final Minigame game) {
            super(game, "net.dandoes.minecraft.minigame.event.minigame.reset");
        }
    }

}
