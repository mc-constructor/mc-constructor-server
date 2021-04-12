package net.dandoes.minecraft.minigame.event;

import java.util.Arrays;
import java.util.Collection;

import net.dandoes.minecraft.minigame.Minigame;
import net.dandoes.minecraft.nodesupport.NodeInteropEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MinigameEvent extends NodeInteropEvent {

    private final Minigame game;
    private final ITextComponent action;

    private MinigameEvent(final Minigame game, final String actionKey) {
        this.game = game;
        this.action = new TranslationTextComponent("event.minigame.start", game);
    }

    public Minigame getGame() {
        return game;
    }

    public ITextComponent getAction() {
        return action;
    }

    public static class MinigameStartEvent extends MinigameEvent {
        public MinigameStartEvent(final Minigame game) {
            super(game, "event.minigame.start");
        }
    }

}
