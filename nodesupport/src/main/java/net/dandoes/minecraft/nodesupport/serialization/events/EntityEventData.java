package net.dandoes.minecraft.nodesupport.serialization.events;

import net.dandoes.minecraft.nodesupport.serialization.objects.BlockPosData;
import net.dandoes.minecraft.nodesupport.serialization.objects.DamageSourceData;
import net.dandoes.minecraft.nodesupport.serialization.objects.DirectionData;
import net.dandoes.minecraft.nodesupport.serialization.objects.EntityData;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class EntityEventData<TEntityEvent extends EntityEvent> extends EventData<TEntityEvent> {
    public static EntityEventData<?> forEvent(final EntityEvent event) {
        if (event instanceof PlayerInteractEvent) {
            return new PlayerInteractEventData<>((PlayerInteractEvent) event);
        }
        if (event instanceof PlayerEvent) {
            return new PlayerEventData<>((PlayerEvent) event);
        }
        if (event instanceof LivingDeathEvent) {
            return new LivingDeathEventData<>((LivingDeathEvent) event);
        }
        if (event instanceof LivingEvent) {
            return new LivingEventData<>((LivingEvent) event);
        }
        return new EntityEventData<>(event);
    }

    public EntityEventData(final TEntityEvent entityEvent) {
        super(entityEvent);
    }

    public static class LivingEventData<TLivingEvent extends LivingEvent> extends EntityEventData<TLivingEvent> {
        public LivingEventData(final TLivingEvent livingEvent) {
            super(livingEvent);
        }
    }

    public static class LivingDeathEventData<TLivingDeathEvent extends LivingDeathEvent> extends LivingEventData<TLivingDeathEvent> {

        public final DamageSourceData source;
        public final EntityData attacker;

        public LivingDeathEventData(TLivingDeathEvent livingDeathEvent) {
            super(livingDeathEvent);

            this.source = DamageSourceData.forSource(livingDeathEvent.getSource());
            this.attacker = EntityData.forEntity(livingDeathEvent.getEntityLiving().getLastHurtByMob());
        }
    }

    public static class PlayerEventData<TPlayerEvent extends PlayerEvent> extends LivingEventData<TPlayerEvent> {
        public final EntityData.PlayerEntityData player;

        public PlayerEventData(final TPlayerEvent playerEvent) {
            super(playerEvent);

            this.player = new EntityData.PlayerEntityData(playerEvent.getPlayer());
        }
    }

    public static class PlayerInteractEventData<TPlayerInteractEvent extends PlayerInteractEvent> extends PlayerEventData<TPlayerInteractEvent> {
        public final InteractionHand hand;
        public final BlockPosData pos;
        public final DirectionData face;

        public PlayerInteractEventData(final TPlayerInteractEvent playerInteractEvent) {
            super(playerInteractEvent);

            this.hand = playerInteractEvent.getHand();
            this.pos = new BlockPosData(playerInteractEvent.getPos());
            final Direction face = playerInteractEvent.getFace();
            this.face = face == null ? null : new DirectionData(face);
        }
    }
}
