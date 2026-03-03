package tfar.classicbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.network.Message;

@Mod(value = ClassicBar.MODID)
public class ClassicBar {

  public static final String MODID = "classicbar";

  public static final Logger logger = LoggerFactory.getLogger(ClassicBar.MODID);

  public static final ClassicBarsConfig CLIENT;
  public static final ModConfigSpec CLIENT_SPEC;

  static {
    final Pair<ClassicBarsConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClassicBarsConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  public ClassicBar(IEventBus modEventBus, ModContainer container) {
    container.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);

    Message.registerMessages(modEventBus);
    if (FMLEnvironment.dist.isClient()) {
      modEventBus.addListener(this::postInit);
      modEventBus.addListener(EventHandler::setupOverlays);
      modEventBus.addListener(EventHandler::sendModMessage);
    }
  }

  public void postInit(FMLClientSetupEvent event) {
    EventHandler.cacheConfigs();
  }

}
