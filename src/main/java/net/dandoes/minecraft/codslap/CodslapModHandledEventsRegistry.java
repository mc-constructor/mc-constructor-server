package net.dandoes.minecraft.codslap;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class CodslapModHandledEventsRegistry {
    private static final Logger LOGGER = LogManager.getLogger(CodslapModHandledEventsRegistry.class);

    @SubscribeEvent
    public void onKnockbackEvent(final LivingKnockBackEvent event) {
        if (!(event.getAttacker() instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getAttacker();
        // null check: first swing after join / spawn is null sometimes?
        if (player.swingingHand == null) {
            LOGGER.warn("Player.swingingHand was null, assuming main hand");
        } else if (player.swingingHand != Hand.MAIN_HAND) {
            return;
        }
        ItemStack weaponStack = player.getHeldItem(Hand.MAIN_HAND);
        Multimap<String, AttributeModifier> modifiers = weaponStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
        Collection<AttributeModifier> knockbackModifiers = modifiers.get(SharedMonsterAttributes.ATTACK_KNOCKBACK.getName());
        if (knockbackModifiers != null) {
            LOGGER.info("Base knockback: " + event.getStrength());
            float base = getBaseKnockback(knockbackModifiers, event.getStrength());
            event.setStrength(getKnockback(knockbackModifiers, base));
            LOGGER.info("Result knockback: " + event.getStrength());
        }
    }

    private float getBaseKnockback(Collection<AttributeModifier> modifiers, float base) {
        for (AttributeModifier modifier : modifiers) {
            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                base *= modifier.getAmount();
            }
        }
        return base;
    }

    private float getKnockback(Collection<AttributeModifier> modifiers, float base) {
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
