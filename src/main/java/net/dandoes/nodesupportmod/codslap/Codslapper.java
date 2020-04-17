package net.dandoes.nodesupportmod.codslap;

import com.google.common.collect.Multimap;
import net.dandoes.nodesupportmod.ModItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Codslapper extends SwordItem {
    private static final Logger LOGGER = LogManager.getLogger(Codslapper.class);
    protected static final UUID ATTACK_KNOCKBACK_MODIFIER = UUID.fromString("AAF54B27-F135-4D56-908F-2D23111E649B");

    public Codslapper(final String itemId, CodslapperItemTier tier) {
        super(tier, 0, -2.4F,
            new Item.Properties()
                .group(ItemGroup.COMBAT)
                .maxStackSize(1)
                .rarity(Rarity.EPIC)
        );
        ModItem.setItemName(this, itemId);

    }
    @Override
    public ICodslapperItemTier getTier() {
        return (ICodslapperItemTier) super.getTier();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        LOGGER.debug("hitEntity: " + attacker.getName().toString() + " attacking " + target.getName().toString());
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        final Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            AttributeModifier value = new AttributeModifier(
                    ATTACK_KNOCKBACK_MODIFIER,
                    SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(),
                    this.getTier().getKnockbackMultiplier() * 10,
                    AttributeModifier.Operation.MULTIPLY_BASE
            );
            modifiers.put(SharedMonsterAttributes.ATTACK_KNOCKBACK.getName(), value);
        }

        return modifiers;
    }
}
