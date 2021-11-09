package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.core.Direction;

public class DirectionData {
    public final String name;

    public DirectionData(final Direction direction) {
        this.name = direction.getName();
    }
}
