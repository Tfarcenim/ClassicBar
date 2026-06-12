package tfar.classicbar.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ClassicBar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClassicBarsConfig {

  public static ForgeConfigSpec.DoubleValue transitionSpeed;

  public static ForgeConfigSpec.ConfigValue<List<? extends String>> priority;

  public ClassicBarsConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");

    transitionSpeed = builder.defineInRange("transition_speed", 3, 0, Double.MAX_VALUE);

    priority = builder.defineList("priority",() -> new ArrayList<>(BarRegistry.REGISTRY.keySet()),String.class::isInstance);
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent event) {
      EventHandler.cacheConfigs();
      ClassicBar.logger.info("Syncing Classic Bar Configs");
  }
}