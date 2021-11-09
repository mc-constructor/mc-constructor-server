package net.dandoes.minecraft.minigame.serialization;

import net.dandoes.minecraft.minigame.event.MinigameGameClientEvent;
import net.dandoes.minecraft.nodesupport.serialization.events.EventData;

public class MinigameGameClientEventData<TEvent extends MinigameGameClientEvent> extends EventData<TEvent> {
    public final String action;
    public final String game;

    public MinigameGameClientEventData(final TEvent event) {
        super(event);

        this.action = event.getAction().getString();
        this.game = event.getGame().getKey();
    }
}
