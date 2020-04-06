package net.dandoes.nodesupportmod;

import net.dandoes.nodesupportmod.codslap.CodslapItems;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NodeSupportMod.MODID)
public class NodeSupportMod
{
    public static final String MODID = "nodesupportmod";
    public static final String NAME = "NodeJS Support Mod";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private NodeInteropServer interopServer;

    public NodeSupportMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
//        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
//        InterModComms.sendTo("nodesupportmod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) throws Exception {
        this.interopServer = new NodeInteropServer(8888, event.getServer());
        this.interopServer.run();
    }

    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerLoggedOutEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerRespawnEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.ItemPickupEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final AttackEntityEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerInteractEvent.LeftClickBlock event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerInteractEvent.LeftClickEmpty event) {
        // may need a client mod to send this event, it is only fired on the client side
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final EntityItemPickupEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingDeathEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingAttackEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingDamageEvent event) {
        this.interopServer.broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingFallEvent event) {
        this.interopServer.broadcast(event);
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisterEvent) {
            IForgeRegistry<Item> registry = itemRegisterEvent.getRegistry();
            itemRegisterEvent.getRegistry().registerAll(CodslapItems.getItems());
        }
    }

}
