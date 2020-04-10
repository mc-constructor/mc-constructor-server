package net.dandoes.nodesupportmod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import static net.dandoes.nodesupportmod.NodeSupportMod.MODID;

public class NodeSupportModChannel {
    private static final String PROTOCOL_VERSION = "1.0";

    private static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    static {
        int messageId = 0;
//        channel.registerMessage(messageId++, PlayerInteractEvent.LeftClickEmpty::new, )
    }
}
