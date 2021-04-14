package net.dandoes.minecraft.nodesupport;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NodeSupportMod.MODID)
public class NodeSupportMod
{
    public static final String MODID = "dandoes_nodesupport";
    static final String NAME = "NodeJS Support Mod";
    private static final Logger LOGGER = LogManager.getLogger(NodeSupportMod.class);

    private NodeInteropServer interopServer;

    public NodeSupportMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void serverSetup(final FMLDedicatedServerSetupEvent event) {
        if (this.interopServer != null) {
            return;
        }

        LOGGER.debug("Starting NodeInteropServer");
        DedicatedServer server = event.getServerSupplier().get();
        this.interopServer = new NodeInteropServer(server, 8888);

        MinecraftForge.EVENT_BUS.register(new ServerBroadcastedEventsRegistry(this.interopServer));
        MinecraftForge.EVENT_BUS.register(new NodeInteropEventsRegistry(this.interopServer));

        try {
            this.interopServer.run();
        } catch (Exception ex) {
            event.setCanceled(true);
            LOGGER.error(ex);
        }
    }

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

}
