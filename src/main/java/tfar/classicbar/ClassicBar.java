package tfar.classicbar;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
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

@Mod(value = ClassicBar.MODID)
public class ClassicBar {

  public static final String MODID = "classicbar";

  public static final Logger logger = LogManager.getLogger();

  public static final ClassicBarsConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  static {
    final Pair<ClassicBarsConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClassicBarsConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public ClassicBar() {
    ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);

    Message.registerMessages(MODID);
    if (FMLEnvironment.dist.isClient()) {
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::setupOverlays);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(EventHandler::sendModMessage);
    }
  }

  public void postInit(FMLClientSetupEvent event) {
    EventHandler.cacheConfigs();
  }

}
