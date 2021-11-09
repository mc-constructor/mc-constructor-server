package net.dandoes.minecraft.nodesupport;

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onServerSetup);
    }

    public void onServerSetup(final FMLDedicatedServerSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new NodeInteropEventsRegistry());

        this.interopServer = new NodeInteropServer(8888);
        this.interopServer.run();
    }

    @SubscribeEvent
    public void onServerAboutToStart(final FMLServerAboutToStartEvent event) {
        LOGGER.debug("onServerAboutToStart");
        this.interopServer.ready((DedicatedServer) event.getServer());
    }


}
