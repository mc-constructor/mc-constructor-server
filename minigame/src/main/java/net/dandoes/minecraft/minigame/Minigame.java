package net.dandoes.minecraft.minigame;

import net.minecraft.network.chat.Component;

public class Minigame {

    private final String key;
    private final Component title;
    private final Component description;

    public Minigame(final String key, final Component title, final Component description) {
        this.key = key;
        this.title = title;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public Component getTitle() {
        return title;
    }

    public Component getDescription() {
        return description;
    }

}
