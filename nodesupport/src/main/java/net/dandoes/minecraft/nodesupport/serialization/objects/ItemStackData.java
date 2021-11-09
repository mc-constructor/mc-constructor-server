package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class ItemStackData {
    public final String registryName;
    public final String displayName;
    public final int count;

    private static String getName(final ItemStack itemStack) {
        final CompoundTag tag = itemStack.getTag();
        if (tag == null) {
            return null;
        }
        final CompoundTag display = (CompoundTag) tag.get("display");
        if (display == null) {
            return null;
        }
        final ListTag name = (ListTag) display.get("Name");
        if (name == null) {
            return null;
        }

        final StringBuilder text = new StringBuilder();
        for (final Tag part : name) {
            if (part instanceof CompoundTag) {
                text.append(((CompoundTag)part).getString("text"));
            } else {
                text.append(part.toString());
            }
        }
        return text.toString();
    }

    public ItemStackData(final ItemStack itemStack) {
        final Item item = itemStack.getItem();
        this.registryName = Objects.requireNonNull(item.getRegistryName()).toString();
        this.displayName = getName(itemStack);
        this.count = itemStack.getCount();
    }
}
