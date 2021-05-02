package net.dandoes.minecraft.minigame;

import net.dandoes.minecraft.nodesupport.IModNamespacedIdProvider;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
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
            MinigameArgument.class,
            new ArgumentSerializer<>(MinigameArgument::minigameArgument)
        );
    }

}
