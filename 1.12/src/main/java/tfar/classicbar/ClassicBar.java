package tfar.classicbar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import tfar.classicbar.network.SyncHandler;
import tfar.classicbar.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Mod(modid = ClassicBar.MODID, name = ClassicBar.MODNAME, version = ClassicBar.MODVERSION, useMetadata = true, clientSideOnly = true)
public class ClassicBar {

    public static final String MODID = "classicbar";
    public static final String MODNAME = "Classic Bar";
    public static final String MODVERSION = "@VERSION@";

    @SidedProxy(clientSide = "tfar.classicbar.proxy.ClientProxy",serverSide = "tfar.classicbar.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        SyncHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        if (Loader.isModLoaded("mantle") || (Loader.isModLoaded("toughasnails"))) {
            logger.info("Unregistering problematic overlays.");
            ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;
            try {
                Field f = EventBus.class.getDeclaredField("listeners");
                f.setAccessible(true);
                listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>) f.get(MinecraftForge.EVENT_BUS);
                for (Map.Entry<Object, ArrayList<IEventListener>> entry : listeners.entrySet()) {
                    String s = entry.getKey().getClass().getCanonicalName();
                    //System.out.println(s);
                    //System.out.println(entry);
                    if ("slimeknights.mantle.client.ExtraHeartRenderHandler".equals(s)) {
                        logger.info("Unregistered Mantle bar");
                        MinecraftForge.EVENT_BUS.unregister(entry.getKey());
                    }
                    if ("toughasnails.handler.thirst.ThirstOverlayHandler".equals(s)) {
                        logger.info("Unregistered Thirst bar");
                        MinecraftForge.EVENT_BUS.unregister(entry.getKey());
                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}
