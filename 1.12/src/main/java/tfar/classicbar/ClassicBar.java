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

import static tfar.classicbar.config.ModConfig.*;



@Mod(modid = ClassicBar.MODID, name = ClassicBar.MODNAME, version = ClassicBar.MODVERSION, useMetadata = true, clientSideOnly = true)
public class ClassicBar {

    public static final String MODID = "classicbar";
    public static final String MODNAME = "Classic Bar";
    public static final String MODVERSION = "0.0.6";

    @SidedProxy(clientSide = "tfar.classicbar.proxy.ClientProxy")
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
        if (Loader.isModLoaded("advancedrocketry") && warnings.advancedRocketryWarning && general.displayToughnessBar)
            logger.warn("Toughness bar may not display correctly, change the placement in advanced rocketry config." +
                    "This is NOT a bug in the mod.");

        if (Loader.isModLoaded("mantle")) {
            logger.info("Unregistering Mantle health renderer.");
            ConcurrentHashMap<Object, ArrayList<IEventListener>>listeners;

            try {
                Field f = EventBus.class.getDeclaredField("listeners");
                f.setAccessible(true);
                Object handler = null;
                listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>)f.get(MinecraftForge.EVENT_BUS);
                for (Map.Entry<Object, ArrayList<IEventListener>> entry : listeners.entrySet()){
                    //System.out.println(entry);
                    if (entry == null)continue;
                    String s = entry.getKey().getClass().getCanonicalName();
                    if (s == null)continue;
                    //System.out.println(s);
                    System.out.println("Key: "+entry.getKey());
                    System.out.println("Value"+entry.getValue());
if (s.equals("slimeknights.mantle.client.ExtraHeartRenderHandler")) {
    System.out.println("sucess?");
    MinecraftForge.EVENT_BUS.unregister("slimeknights.mantle.client.ExtraHeartRenderHandler");}}

                System.out.println(handler);
                if (handler == null) logger.warn("Unable to unregister Mantle health renderer!");
                else MinecraftForge.EVENT_BUS.unregister(handler);
            } catch (IllegalAccessException | NoSuchFieldException | NullPointerException e) {e.printStackTrace(); }
            proxy.postInit(event);
        }
    }
}
