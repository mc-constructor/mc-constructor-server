package net.dandoes.minecraft.nodesupport;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerBroadcastedEventsRegistry {

    private final NodeInteropServer interopServer;

    public ServerBroadcastedEventsRegistry(final NodeInteropServer interopServer) {
        this.interopServer = interopServer;
    }

    private void broadcast(Event event) {
        this.interopServer.broadcast(event);
    }

    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerLoggedOutEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.PlayerRespawnEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerEvent.ItemPickupEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final AttackEntityEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerInteractEvent.LeftClickBlock event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final PlayerInteractEvent.LeftClickEmpty event) {
        // may need a client mod to send this event, it is only fired on the client side
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final EntityItemPickupEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingDeathEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingAttackEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingDamageEvent event) {
        broadcast(event);
    }
    @SubscribeEvent
    public void onBroadcastEvent(final LivingFallEvent event) {
        broadcast(event);
    }
}
