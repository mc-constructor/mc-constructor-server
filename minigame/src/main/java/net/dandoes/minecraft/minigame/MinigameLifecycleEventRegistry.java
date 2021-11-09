package net.dandoes.minecraft.minigame;

import net.dandoes.minecraft.minigame.serialization.MinigameArgumentSerializer;
import net.dandoes.minecraft.nodesupport.IModNamespacedIdProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class MinigameLifecycleEventRegistry {

    private final IModNamespacedIdProvider mod;

    MinigameLifecycleEventRegistry(final IModNamespacedIdProvider mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent event) {
        ArgumentTypes.register(
            this.mod.getModNamespacedId("minigame"),
            MinigameArgumentType.class,
            new MinigameArgumentSerializer()
        );
    }

}
