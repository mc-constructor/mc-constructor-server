package net.dandoes.minecraft.minigame;

import net.dandoes.minecraft.nodesupport.NodeInteropCommandEvent;
import net.dandoes.minecraft.nodesupport.NodeInteropClientDisconnectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MinigameNodeInteropEventsRegistry {

    @SubscribeEvent
    public void onInteropCommand(final NodeInteropCommandEvent event) {
        MinigameManager.handleCommand(event);
    }

    @SubscribeEvent
    public void onInteropCommand(final NodeInteropClientDisconnectEvent event) {
        MinigameManager.removeInteropClient(event.getInteropClient());
    }

}
