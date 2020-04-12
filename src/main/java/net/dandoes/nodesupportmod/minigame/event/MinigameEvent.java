package net.dandoes.nodesupportmod.minigame.event;

import net.dandoes.nodesupportmod.minigame.Minigame;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.Event;

public class MinigameEvent extends Event {

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
