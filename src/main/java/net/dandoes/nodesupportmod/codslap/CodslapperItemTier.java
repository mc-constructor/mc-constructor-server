package net.dandoes.nodesupportmod.codslap;

import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

public enum CodslapperItemTier implements ICodslapperItemTier {
    WOOD_CODSLAPPER(1),
    STONE_CODSLAPPER(2),
    IRON_CODSLAPPER(3),
    GOLD_CODSLAPPER(4),
    DIAMOND_CODSLAPPER(5),
    CODSLAPPER(6);

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;
    private final double knockbackMultiplier;

    private CodslapperItemTier(double knockbackMultiplier) {
        this.harvestLevel = 0;
        this.maxUses = Integer.MAX_VALUE;
        this.efficiency = 0;
        this.attackDamage = 0F;
        this.enchantability = 0;
        this.repairMaterial = new LazyValue<>(() -> Ingredient.fromItems(Items.COD, Items.COD_BUCKET));
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public float getEfficiency() {
        return this.efficiency;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public Ingredient getRepairMaterial() {
        return this.repairMaterial.getValue();
    }

    public double getKnockbackMultiplier() {
        return this.knockbackMultiplier;
    }
}