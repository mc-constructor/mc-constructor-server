package net.dandoes.minecraft.minigame.serialization;

import com.google.gson.JsonObject;
import net.dandoes.minecraft.minigame.MinigameArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class MinigameArgumentSerializer implements ArgumentSerializer<MinigameArgumentType> {

    @Override
    public void serializeToNetwork(MinigameArgumentType arg, FriendlyByteBuf buf) {
        buf.writeEnum(arg.getType());

    }

    @Override
    public MinigameArgumentType deserializeFromNetwork(FriendlyByteBuf buf) {
        MinigameArgumentType.MinigameType type = buf.readEnum(MinigameArgumentType.MinigameType.class);
        return new MinigameArgumentType(type);
    }

    @Override
    public void serializeToJson(MinigameArgumentType arg, JsonObject obj) {
        obj.addProperty("type", "minigame_name");
    }
}
