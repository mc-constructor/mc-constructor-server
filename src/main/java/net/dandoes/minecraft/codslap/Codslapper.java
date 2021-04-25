package net.dandoes.minecraft.codslap;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Codslapper extends SwordItem {
    private static final Logger LOGGER = LogManager.getLogger(Codslapper.class);
    protected static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("AAF54B27-F135-4D56-908F-2D23111E649B");

    public Codslapper(final String itemId, CodslapperItemTier tier) {
        super(tier, 0, -2.4F,
            new Item.Properties()
                .tab(ItemGroup.TAB_COMBAT)
                .stacksTo(1)
                .rarity(Rarity.EPIC)
        );
        ModItem.setItemName(this, itemId);

    }
    @Override
    public ICodslapperItemTier getTier() {
        return (ICodslapperItemTier) super.getTier();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        LOGGER.debug("hitEntity: " + attacker.getName().toString() + " attacking " + target.getName().toString());
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
        final Multimap<Attribute, AttributeModifier> baseModifiers = super.getDefaultAttributeModifiers(equipmentSlot);
        if (equipmentSlot != EquipmentSlotType.MAINHAND) {
            return baseModifiers;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(baseModifiers);
        AttributeModifier value = new AttributeModifier(
                "Codslap knockback",
                this.getTier().getKnockbackMultiplier(),
                AttributeModifier.Operation.MULTIPLY_BASE
        );
        builder.put(Attributes.ATTACK_KNOCKBACK, value);
        return builder.build();
    }
}
