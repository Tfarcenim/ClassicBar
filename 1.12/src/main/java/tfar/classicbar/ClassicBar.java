package tfar.classicbar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.relauncher.Side;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.network.SyncHandler;
import tfar.classicbar.overlays.*;
import tfar.classicbar.overlays.modoverlays.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.stream;
import static tfar.classicbar.config.ModConfig.general;

@Mod.EventBusSubscriber(Side.CLIENT)
@Mod(modid = ClassicBar.MODID, name = ClassicBar.MODNAME, version = ClassicBar.MODVERSION, useMetadata = true, clientSideOnly = true)
public class ClassicBar {

  public static final String MODID = "classicbar";
  public static final String MODNAME = "Classic Bar";
  public static final String MODVERSION = "@VERSION@";
  public static final String[] problemMods = new String[]{"mantle", "toughasnails"};
  public static final boolean TOUGHASNAILS = Loader.isModLoaded("toughasnails");

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

    MinecraftForge.EVENT_BUS.register(new ModConfig.ConfigEventHandler());
    //Register renderers for events
    ClassicBar.logger.info("Registering Vanilla Overlays");
    MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
    MinecraftForge.EVENT_BUS.register(new HealthBarMountRenderer());
    MinecraftForge.EVENT_BUS.register(new ArmorBarRenderer());
    if (general.overlays.displayToughnessBar) MinecraftForge.EVENT_BUS.register(new ArmorToughnessBarRenderer());
    MinecraftForge.EVENT_BUS.register(new HungerBarRenderer());

    //mod renderers
    if (Loader.isModLoaded("randomthings")) {
      if (Loader.isModLoaded("baubles"))
        MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());
      else MinecraftForge.EVENT_BUS.register(new LavaCharmNoBaublesRenderer());
    }
    if (Loader.isModLoaded("lavawaderbauble")) {
      MinecraftForge.EVENT_BUS.register(new LavaWaderBaubleRenderer());
    }

    //if (Loader.isModLoaded("superiorshields"))
    //  MinecraftForge.EVENT_BUS.register(new SuperiorShieldRenderer());
    if (Loader.isModLoaded("toughasnails"))
      MinecraftForge.EVENT_BUS.register(new ThirstBarRenderer());
  //MinecraftForge.EVENT_BUS.register(new BetterDivingRenderer());
    MinecraftForge.EVENT_BUS.register(new OxygenBarRenderer());
    if (Loader.isModLoaded("botania")) MinecraftForge.EVENT_BUS.register(new TiaraBarRenderer());

    boolean areProblemModsPresent = stream(problemMods).anyMatch(Loader::isModLoaded);
    if (areProblemModsPresent) {
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
      } catch (IllegalAccessException | NoSuchFieldException e) {
        e.printStackTrace();
      }
    }
  }
}