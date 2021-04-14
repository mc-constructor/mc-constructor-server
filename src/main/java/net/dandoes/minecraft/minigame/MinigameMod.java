package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MinigameMod.MODID)
public class MinigameMod {
    static final String MODID = "dandoes_minigame";

    public MinigameMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void serverSetup(final FMLDedicatedServerSetupEvent event) {
        final CommandDispatcher<CommandSource> dispatcher = event.getServerSupplier().get().getCommandManager().getDispatcher();
        MinigameCommand.register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new MinigameNodeInteropEventsRegistry());
    }

}
