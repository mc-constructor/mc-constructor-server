package net.dandoes.nodesupportmod;

import net.minecraft.item.Item;

public class ModItem {
    public static void setItemName(final Item item, final String itemName) {
        item.setRegistryName(NodeSupportMod.MODID, itemName);
    }
}
