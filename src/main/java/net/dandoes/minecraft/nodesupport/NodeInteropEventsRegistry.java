package net.dandoes.minecraft.nodesupport;

import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NodeInteropEventsRegistry {

    @SubscribeEvent
    public void onClientSubscriptionCommandEvent(final NodeInteropClientSubscriptionCommandEvent event) {
        final String cmd = event.getCmd();
        final String[] parts = cmd.split(" ");
        final NodeInteropClientSubscriptionAction action = NodeInteropClientSubscriptionAction.valueOf(parts[0]);
        final String eventClassName = parts[1];
        final NodeInteropClientSubscriptionEvent subEvent = new NodeInteropClientSubscriptionEvent(event.getSource(), action, eventClassName);
        MinecraftForge.EVENT_BUS.post(subEvent);
    }

    @SubscribeEvent
    public void onClientSubscribeEvent(final NodeInteropClientSubscriptionEvent event) {
        final NodeInteropClient interopClient = event.getInteropClient();
        final NodeCommandSource source = event.getSource();
        try {
            final Class<? extends Event> eventClass = event.getEventClass();
            switch(event.getAction()) {
                case add:
                    interopClient.subscribeToEvent(eventClass);
                    break;
                case remove:
                    interopClient.unsubscribeFromEvent(eventClass);
                    break;
            }
            source.sendFeedback(new StringTextComponent("ok"), true);
        } catch (ClassNotFoundException ex) {
            interopClient.sendResponse(source, ex);
            ex.printStackTrace();
        }
    }
}
