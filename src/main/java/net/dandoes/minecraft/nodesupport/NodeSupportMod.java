package net.dandoes.minecraft.nodesupport;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NodeSupportMod.MODID)
public class NodeSupportMod
{
    public static final String MODID = "dandoes_nodesupport";
    static final String NAME = "NodeJS Support Mod";
    private static final Logger LOGGER = LogManager.getLogger(NodeSupportMod.class);

    private NodeInteropServer interopServer;

    public NodeSupportMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(new NodeInteropEventsRegistry());
    }

    @SubscribeEvent
    public void serverSetup(final FMLDedicatedServerSetupEvent event) {
        if (this.interopServer != null) {
            return;
        }

        LOGGER.debug("Starting NodeInteropServer");
        DedicatedServer server = event.getServerSupplier().get();
        this.interopServer = new NodeInteropServer(server, 8888);

        try {
            this.interopServer.run();
        } catch (Exception ex) {
            event.setCanceled(true);
            LOGGER.error(ex);
        }
    }

}
