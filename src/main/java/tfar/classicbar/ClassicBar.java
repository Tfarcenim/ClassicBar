package tfar.classicbar;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.network.Message;
import tfar.classicbar.network.SyncHandler;
import tfar.classicbar.overlays.*;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tfar.classicbar.config.ModConfig.general;

@Mod(value = ClassicBar.MODID)
public class ClassicBar {

  public static final String MODID = "classicbar";
  public static final String[] problemMods = new String[]{"mantle"};

//  public static final boolean TOUGHASNAILS = ModList.get().isLoaded("toughasnails");
 // public static final boolean IBLIS = ModList.get().isLoaded("iblis");
  public static boolean BAUBLES;

  public static Logger logger = LogManager.getLogger();

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class Setup {

    @SubscribeEvent
    public static void postInit(FMLCommonSetupEvent event) {
      BAUBLES = ModList.get().isLoaded("baubles");
      Message.registerMessages(MODID);

      MinecraftForge.EVENT_BUS.register(new SyncHandler());
      MinecraftForge.EVENT_BUS.register(new ModConfig.ConfigEventHandler());
      //Register renderers for events
      ClassicBar.logger.info("Registering Vanilla Overlays");
      MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
      MinecraftForge.EVENT_BUS.register(new HealthBarMountRenderer());
      MinecraftForge.EVENT_BUS.register(new ArmorBarRenderer());
      if (general.overlays.displayToughnessBar) MinecraftForge.EVENT_BUS.register(new ArmorToughnessBarRenderer());
      MinecraftForge.EVENT_BUS.register(new HungerBarRenderer());
      MinecraftForge.EVENT_BUS.register(new OxygenBarRenderer());

      //mod renderers
      ClassicBar.logger.info("Registering Mod Overlays");
      // if (ModList.get().isLoaded("randomthings")) MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());
      if (ModList.get().isLoaded("lavawaderbauble")) {
        //  MinecraftForge.EVENT_BUS.register(new LavaWaderBaubleRenderer());
      }

      //if (ModList.get().isLoaded("superiorshields"))
      //  MinecraftForge.EVENT_BUS.register(new SuperiorShieldRenderer());

        //MinecraftForge.EVENT_BUS.register(new BetterDivingRenderer());
      //  if (ModList.get().isLoaded("botania")) MinecraftForge.EVENT_BUS.register(new TiaraBarRenderer());

      boolean areProblemModsPresent = Arrays.stream(problemMods).anyMatch(ModList.get()::isLoaded);
      if (areProblemModsPresent) {
        logger.info("Unregistering problematic overlays.");
        ConcurrentHashMap<Object, List<IEventListener>> listeners;
        try {
          Field f = EventBus.class.getDeclaredField("listeners");
          f.setAccessible(true);
          listeners = (ConcurrentHashMap<Object, List<IEventListener>>) f.get(MinecraftForge.EVENT_BUS);
          for (Map.Entry<Object, List<IEventListener>> entry : listeners.entrySet()) {
            String s = entry.getKey().getClass().getCanonicalName();
            //System.out.println(s);
            //System.out.println(entry);

            if ("slimeknights.mantle.client.ExtraHeartRenderHandler".equals(s)) {
              logger.info("Unregistered Mantle bar");
              MinecraftForge.EVENT_BUS.unregister(entry.getKey());
            }
          }
        } catch (IllegalAccessException | NoSuchFieldException e) {
          e.printStackTrace();
        }
      }
    }
  }
}