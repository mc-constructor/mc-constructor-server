package net.dandoes.nodesupportmod.minigame;

import net.minecraft.util.text.ITextComponent;

public class Minigame {

    private final String key;
    private final ITextComponent title;
    private final ITextComponent description;

    public Minigame(String key, ITextComponent title, ITextComponent description) {
        this.key = key;
        this.title = title;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public ITextComponent getTitle() {
        return title;
    }

    public ITextComponent getDescription() {
        return description;
    }

}
