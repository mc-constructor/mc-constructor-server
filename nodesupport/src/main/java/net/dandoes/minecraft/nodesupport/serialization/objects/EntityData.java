package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class EntityData {
    public static EntityData forEntity(final Entity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof ServerPlayer) {
            return new ServerPlayerEntityData((ServerPlayer) entity);
        }
        if (entity instanceof Player) {
            return new PlayerEntityData((Player) entity);
        }
        if (entity instanceof LivingEntity) {
            return new LivingEntityData((LivingEntity) entity);
        }
        return new EntityData(entity);
    }

    final TextComponentData name;

    public EntityData(final Entity entity) {
        this.name = TextComponentData.forComponent(entity.getName());
    }

    public static class LivingEntityData extends EntityData {
        public final ItemStackData mainHandItem;
        public final ItemStackData offhandItem;

        public LivingEntityData(final LivingEntity livingEntity) {
            super(livingEntity);

            this.mainHandItem = new ItemStackData(livingEntity.getMainHandItem());
            this.offhandItem = new ItemStackData(livingEntity.getOffhandItem());
        }

    }

    public static class PlayerEntityData extends LivingEntityData {
        public final UUID id;
        public final int experienceLevel;
        public final int totalExperience;

        public PlayerEntityData(final Player playerEntity) {
            super(playerEntity);
            this.id = playerEntity.getUUID();
            this.experienceLevel = playerEntity.experienceLevel;
            this.totalExperience = playerEntity.totalExperience;
        }
    }

    public static class ServerPlayerEntityData extends PlayerEntityData {
        public ServerPlayerEntityData(final ServerPlayer serverPlayerEntity) {
            super(serverPlayerEntity);
        }
    }
}
