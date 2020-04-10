package net.dandoes.nodesupportmod;

import com.google.common.collect.Multimap;
import net.dandoes.nodesupportmod.codslap.CodslapItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
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

import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NodeSupportMod.MODID)
public class NodeSupportMod
{
    public static final String MODID = "nodesupportmod";
    public static final String NAME = "NodeJS Support Mod";

    private static final Logger LOGGER = LogManager.getLogger(NodeSupportMod.class);

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
//        LOGGER.info("HELLO FROM PREINIT");
//        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
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
//    @Mod.EventBusSubscriber(value= Dist.CLIENT)
//    public static class ClientBroadcastedEventsRegistry {
//        @SubscribeEvent
//        public static void onBroadcastEvent(final PlayerInteractEvent.LeftClickEmpty event) {
//            // may need a client mod to send this event, it is only fired on the client side
//            broadcast(event);
//        }
//
//        @SubscribeEvent
//        public static void onConnectToServer(final NetworkEvent event) {
//            net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//        }
//    }

    @Mod.EventBusSubscriber()
    public static class ModHandledEventsRegistry {
        @SubscribeEvent
        public static void onKnockbackEvent(final LivingKnockBackEvent event) {
            if (!(event.getAttacker() instanceof ServerPlayerEntity)) {
                return;
            }
            ServerPlayerEntity player = (ServerPlayerEntity) event.getAttacker();
            if (player.swingingHand != Hand.MAIN_HAND) {
                return;
            }
            ItemStack weaponStack = player.getHeldItem(Hand.MAIN_HAND);
            Multimap<String, AttributeModifier> modifiers = weaponStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
            Collection<AttributeModifier> knockbackModifiers = modifiers.get(SharedMonsterAttributes.ATTACK_KNOCKBACK.getName());
            if (knockbackModifiers != null) {
                LOGGER.debug("Base knockback: " + event.getStrength());
                float base = getBaseKnockback(knockbackModifiers, event.getStrength());
                event.setStrength(getKnockback(knockbackModifiers, base));
                LOGGER.debug("Result knockback: " + event.getStrength());
            }
        }

        private static float getBaseKnockback(Collection<AttributeModifier> modifiers, float base) {
            for (AttributeModifier modifier : modifiers) {
                if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                    base *= modifier.getAmount();
                }
            }
            return base;
        }

        private static float getKnockback(Collection<AttributeModifier> modifiers, float base) {
            float result = base;
            for (AttributeModifier modifier : modifiers) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                    result += modifier.getAmount();
                }
                if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    result += modifier.getAmount();
                }
            }
            return result;
        }
    }

    @Mod.EventBusSubscriber(value=Dist.DEDICATED_SERVER)
    public static class ServerBroadcastedEventsRegistry {
        private static NodeInteropServer interopServer;

        private static void broadcast(Event event) {
            interopServer.broadcast(event);
        }

        @SubscribeEvent
        public static void onServerStarting(FMLServerStartingEvent event) throws Exception {
            LOGGER.debug("Starting NodeInteropServer");
            interopServer = new NodeInteropServer(8888, event.getServer());
            interopServer.run();
        }

        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerEvent.PlayerLoggedInEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerEvent.PlayerLoggedOutEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerEvent.PlayerRespawnEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerEvent.ItemPickupEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final AttackEntityEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerInteractEvent.LeftClickBlock event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final PlayerInteractEvent.LeftClickEmpty event) {
            // may need a client mod to send this event, it is only fired on the client side
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final EntityItemPickupEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final LivingDeathEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final LivingAttackEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final LivingDamageEvent event) {
            broadcast(event);
        }
        @SubscribeEvent
        public static void onBroadcastEvent(final LivingFallEvent event) {
            broadcast(event);
        }

    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegisterEvent) {
            IForgeRegistry<Item> registry = itemRegisterEvent.getRegistry();
            registry.registerAll(CodslapItems.getItems());
        }
    }

}
