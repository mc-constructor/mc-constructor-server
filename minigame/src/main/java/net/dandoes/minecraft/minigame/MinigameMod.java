package net.dandoes.minecraft.minigame;

import com.mojang.brigadier.CommandDispatcher;
import net.dandoes.minecraft.nodesupport.IModNamespacedIdProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

@Mod(MinigameMod.MODID)
public class MinigameMod implements IModNamespacedIdProvider {
    static final String MODID = "dandoes_minigame";

    public MinigameMod() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(new MinigameLifecycleEventRegistry(this));
    }

    public String getModNamespacedId(final String id) {
        return String.format("%s:%s", MODID, id);
    }

    @SubscribeEvent
    public void serverSetup(final FMLServerAboutToStartEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();
        MinigameManagerCommand.register(dispatcher);
        MinigameCommand.register(dispatcher);
        MinecraftForge.EVENT_BUS.register(new MinigameNodeInteropEventsRegistry());
    }

}
