package tfar.classicbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen; // Added: NeoForge built-in config screen UI
import net.neoforged.neoforge.client.gui.IConfigScreenFactory; // Added: extension point interface that wires ConfigurationScreen into the Mods menu
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.network.Message;

@Mod(value = ClassicBar.MODID)
public class ClassicBar {

  public static final String MODID = "classicbar";

  // Changed: switched from Log4j (LogManager.getLogger()) to SLF4J (LoggerFactory) per NeoForge 1.21.1 convention
  public static final Logger logger = LoggerFactory.getLogger(ClassicBar.MODID);

  public static final ClassicBarsConfig CLIENT;
  // Changed: ForgeConfigSpec -> ModConfigSpec (NeoForge renamed the class)
  public static final ModConfigSpec CLIENT_SPEC;

  static {
    // Changed: ForgeConfigSpec.Builder -> ModConfigSpec.Builder (class rename in NeoForge)
    final Pair<ClassicBarsConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClassicBarsConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
  }

  // Changed: constructor now receives IEventBus and ModContainer via injection instead of
  // calling FMLJavaModLoadingContext.get().getModEventBus() internally. NeoForge 1.21+
  // passes these as constructor parameters. IExtensionPoint.DisplayTest removed — NeoForge
  // no longer requires mods to declare client-only status this way.
  public ClassicBar(IEventBus modEventBus, ModContainer container) {
    container.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC); // Changed: was ModLoadingContext.get().registerConfig()

    Message.registerMessages(modEventBus); // Changed: now passes the mod event bus; old API used a channel name string
    if (FMLEnvironment.dist.isClient()) {
      // Added: registers ConfigurationScreen as the config UI for this mod; exposes the Config button in Mods > classicbar
      // ConfigurationScreen::new matches IConfigScreenFactory.createScreen(ModContainer, Screen) via constructor reference
      container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
      modEventBus.addListener(this::postInit);
      modEventBus.addListener(EventHandler::setupOverlays);
      modEventBus.addListener(EventHandler::sendModMessage);
    }
  }

  public void postInit(FMLClientSetupEvent event) {
    EventHandler.cacheConfigs();
  }

}
