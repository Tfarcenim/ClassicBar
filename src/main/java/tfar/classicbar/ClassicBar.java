package tfar.classicbar;

import de.teamlapen.vampirism.util.Helper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.network.Message;
import tfar.classicbar.overlays.mod.Blood;
import tfar.classicbar.overlays.vanilla.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Mod(value = ClassicBar.MODID)
public class ClassicBar {

  public static final String MODID = "classicbar";
  public static final String[] problemMods = new String[]{"mantle"};

  public ClassicBar() {
    ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CLIENT_SPEC);
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
  }

  public static final ModConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  static {
    final Pair<ModConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ModConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static Logger logger = LogManager.getLogger();

  public void postInit(FMLClientSetupEvent event) {
    Message.registerMessages(MODID);

    //Register renderers for events
    ClassicBar.logger.info("Registering Vanilla Overlay");
    MinecraftForge.EVENT_BUS.register(new EventHandler());

    EventHandler.registerAll(new Absorption(), new Air(), new Armor(), new ArmorToughness(),
            new Health(), new Hunger(), new MountHealth());
    if (Helpers.vampirismloaded)EventHandler.register(new Blood());

    //mod renderers
    ClassicBar.logger.info("Registering Mod Overlays");

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
    EventHandler.setup();
  }
}