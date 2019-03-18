package tfar.classicbar;

import tfar.classicbar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ClassicBar.MODID, name = ClassicBar.MODNAME, version = ClassicBar.MODVERSION, useMetadata = true, clientSideOnly = true)
public class ClassicBar
{

    public static final String MODID = "classicbar";
    public static final String MODNAME = "Classic Bar";
    public static final String MODVERSION = "0.0.2";

    @SidedProxy(clientSide = "tfar.classicbar.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
      /*  if (Loader.isModLoaded("mantle")) {
            logger.info("Unregistering Mantle health renderer.");
            Field f = EventBus::class.java.getDeclaredField("listeners");
            f.setAccessible(true);
            val listeners = f.get(MinecraftForge.EVENT_BUS) as ConcurrentHashMap<*, *>
            val handler = listeners.keys.firstOrNull { it.javaClass.canonicalName == "slimeknights.mantle.client.ExtraHeartRenderHandler" }
            if (handler == null) LOGGER.warn("Unable to unregister Mantle health renderer!")
            else MinecraftForge.EVENT_BUS.unregister(handler) */
        proxy.postInit(event);
    }
}
