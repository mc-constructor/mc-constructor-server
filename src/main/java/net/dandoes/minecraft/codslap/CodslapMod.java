package net.dandoes.minecraft.codslap;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(CodslapMod.MODID)
public class CodslapMod {

    static final String MODID = "dandoes_minigame_codslap";

    public CodslapMod() {
        MinecraftForge.EVENT_BUS.register(new CodslapModHandledEventsRegistry());
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisterEvent) {
            IForgeRegistry<Item> registry = itemRegisterEvent.getRegistry();
            registry.registerAll(CodslapItems.getItems());
        }
    }
}
