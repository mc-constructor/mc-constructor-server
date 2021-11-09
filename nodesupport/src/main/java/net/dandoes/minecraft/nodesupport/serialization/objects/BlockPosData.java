package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.core.BlockPos;

public class BlockPosData {
    public final int x;
    public final int y;
    public final int z;

    public BlockPosData(final BlockPos blockPos) {
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }
}
