package net.dandoes.minecraft.codslap;

import net.minecraft.world.item.Item;

public class ModItem {
    public static void setItemName(final Item item, final String itemName) {
        item.setRegistryName(CodslapMod.MODID, itemName);
    }
}
