package net.dandoes.minecraft.minigame;

import net.dandoes.minecraft.nodesupport.NodeInteropCommandException;

public class MinigameRegistrationKeyConflictException extends NodeInteropCommandException {

    private static final String ERROR_TEXT_KEY = "net.dandoes.minecraft.minigame.error.registrationKeyConflict";

    @Override
    public String getErrorTextKey() {
        return ERROR_TEXT_KEY;
    }
}
