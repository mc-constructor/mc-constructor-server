package net.dandoes.minecraft.nodesupport;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NodeSupportModChannel {
    private static final String PROTOCOL_VERSION = "1.0";

    private static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NodeSupportMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    static {
        int messageId = 0;
//        channel.registerMessage(messageId++, PlayerInteractEvent.LeftClickEmpty::new, )
    }
}
