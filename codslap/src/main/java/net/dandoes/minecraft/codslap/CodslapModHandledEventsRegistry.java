package net.dandoes.minecraft.codslap;

import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class CodslapModHandledEventsRegistry {
    private static final Logger LOGGER = LogManager.getLogger(CodslapModHandledEventsRegistry.class);

    @SubscribeEvent
    public void onKnockbackEvent(final LivingKnockBackEvent event) {
        final LivingEntity attacker = event.getEntityLiving().getLastHurtByMob();
        if (!(attacker instanceof final ServerPlayer player)) {
            return;
        }
        // null check: first swing after join / spawn is null sometimes?
        if (player.swingingArm == null) {
            LOGGER.warn("Player.swingingHand was null, assuming main hand");
        } else if (player.swingingArm != InteractionHand.MAIN_HAND) {
            return;
        }
        final ItemStack weaponStack = player.getMainHandItem();
        final Multimap<Attribute, AttributeModifier> modifiers = weaponStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
        final Collection<AttributeModifier> knockbackModifiers = modifiers.get(Attributes.ATTACK_KNOCKBACK);
        if (knockbackModifiers != null) {
            LOGGER.info("Base knockback: " + event.getStrength());
            final float base = getBaseKnockback(knockbackModifiers, event.getStrength());
            event.setStrength(getKnockback(knockbackModifiers, base));
            LOGGER.info("Result knockback: " + event.getStrength());
        }
    }

    private float getBaseKnockback(final Collection<AttributeModifier> modifiers, float base) {
        for (final AttributeModifier modifier : modifiers) {
            if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                base *= modifier.getAmount();
            }
        }
        return base;
    }

    private float getKnockback(final Collection<AttributeModifier> modifiers, final float base) {
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
