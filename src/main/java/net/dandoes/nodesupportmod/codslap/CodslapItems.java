package net.dandoes.nodesupportmod.codslap;

import net.dandoes.nodesupportmod.NodeSupportMod;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

@ObjectHolder(NodeSupportMod.MODID)
public class CodslapItems {
    private static final Set<Item> ITEMS = new HashSet<>();

    @ObjectHolder("wood_codslapper")
    public static final Codslapper WOOD_CODSLAPPER = addItem(new Codslapper("wood_codslapper", CodslapperItemTier.WOOD_CODSLAPPER));
    @ObjectHolder("stone_codslapper")
    public static final Codslapper STONE_CODSLAPPER = addItem(new Codslapper("stone_codslapper", CodslapperItemTier.STONE_CODSLAPPER));
    @ObjectHolder("iron_codslapper")
    public static final Codslapper IRON_CODSLAPPER = addItem(new Codslapper("iron_codslapper", CodslapperItemTier.IRON_CODSLAPPER));
    @ObjectHolder("golden_codslapper")
    public static final Codslapper GOLD_CODSLAPPER = addItem(new Codslapper("golden_codslapper", CodslapperItemTier.GOLD_CODSLAPPER));
    @ObjectHolder("diamond_codslapper")
    public static final Codslapper DIAMOND_CODSLAPPER = addItem(new Codslapper("diamond_codslapper", CodslapperItemTier.DIAMOND_CODSLAPPER));
    @ObjectHolder("codslapper")
    public static final Codslapper CODSLAPPER = addItem(new Codslapper("codslapper", CodslapperItemTier.CODSLAPPER));

    private static <TItem extends Item> TItem addItem(TItem item) {
        ITEMS.add(item);
        return item;
    }

    public static Item[] getItems() {
        return ITEMS.toArray(new Item[0]);
    }
}
