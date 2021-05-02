package net.dandoes.minecraft.codslap;

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

    private static final double BASE_KNOCKBACK_MULTIPLIER = 5;

    private final int uses;
    private final float speed;
    private final float attackDamage;
    private final int enchantability;
    private final LazyValue<Ingredient> repairMaterial;
    private final double knockbackMultiplier;

    CodslapperItemTier(double knockbackMultiplier) {
        this.uses = Integer.MAX_VALUE;
        this.speed = 0;
        this.attackDamage = 0F;
        this.enchantability = 0;
        this.repairMaterial = new LazyValue<>(() -> Ingredient.of(Items.COD, Items.COD_BUCKET));
        this.knockbackMultiplier = knockbackMultiplier;
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.attackDamage;
    }

    public int getLevel() {
        return 0;
    }

    public int getEnchantmentValue() {
        return this.enchantability;
    }

    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    public double getKnockbackMultiplier() {
        return BASE_KNOCKBACK_MULTIPLIER * this.knockbackMultiplier;
    }

}