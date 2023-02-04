package tfar.classicbar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.network.Message;
import tk.yongangame.mc.forge.forwardlib.ForwardPlayerData;
import tk.yongangame.mc.forge.forwardlib.ListenerService;


@Mod(value = ClassicBar.MODID)
public class ClassicBar {
  public static final String MODID = "classicbar";

  public ClassicBar() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
   // ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

    if(FMLEnvironment.dist.isClient()) {
      ForwardPlayerData forward = ForwardPlayerData.getInstance();
      MinecraftForge.EVENT_BUS.register(new ListenerService());

      FMLJavaModLoadingContext.get().getModEventBus().addListener(forward::clientSetup);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);

      EventHandler.setupOverlays();
    }
  }

  public static final ClassicBarsConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  static {
    final Pair<ClassicBarsConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClassicBarsConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public static Logger logger = LogManager.getLogger();

  public void postInit(FMLClientSetupEvent event) {
    Message.registerMessages(MODID);
    EventHandler.cacheConfigs();
  }
}