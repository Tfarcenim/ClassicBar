package tfar.classicbar.config;

import com.google.common.collect.Lists;
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

  public static ForgeConfigSpec.BooleanValue showHeldDrinkOverlay;

  public static ForgeConfigSpec.DoubleValue transitionSpeed;
  static ForgeConfigSpec.ConfigValue<String> hungerBarColor;
  static ForgeConfigSpec.ConfigValue<String> hungerBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<String> saturationBarColor;
  static ForgeConfigSpec.ConfigValue<String> saturationBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<String> thirstBarColor;
  static ForgeConfigSpec.ConfigValue<String> thirstBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<String> hydrationBarColor;
  static ForgeConfigSpec.ConfigValue<String> hydrationBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> armorColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> armorToughnessColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionPoisonColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionWitherColors;

  public static ForgeConfigSpec.ConfigValue<List<? extends String>> priority;

  public ClassicBarsConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");

    showHeldDrinkOverlay = builder.define("show_held_drink_overlay", true);
    transitionSpeed = builder.defineInRange("transition_speed", 3, 0, Double.MAX_VALUE);

    hungerBarColor = builder.define("hunger_bar_color","#B34D00",String.class::isInstance);
    hungerBarDebuffColor = builder.define("hunger_bar_debuff_color","#249016",String.class::isInstance);
    thirstBarColor = builder.define("thirstr_bar_color","#1C5EE4",String.class::isInstance);
    thirstBarDebuffColor = builder.define("thirst_bar_debuff_color","#5A891C",String.class::isInstance);
    saturationBarColor = builder.define("saturation_bar_color","#FFCC00",String.class::isInstance);
    saturationBarDebuffColor = builder.define("saturation_bar_debuff_color","#87BC00",String.class::isInstance);
    hydrationBarColor = builder.define("hydration_bar_color","#00A3E2",String.class::isInstance);
    hydrationBarDebuffColor = builder.define("hydration_bar_debuff_color","#85CF25",String.class::isInstance);

    armorColors = builder.defineList("armor_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),String.class::isInstance);
    armorToughnessColors = builder.defineList("armor_toughness_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),String.class::isInstance);
    absorptionColors = builder.defineList("absorption_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);
    absorptionPoisonColors = builder.defineList("absorption_poison_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);
    absorptionWitherColors = builder.defineList("absorption_wither_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);

    priority = builder.defineList("priority",() -> new ArrayList<>(BarRegistry.REGISTRY.keySet()),String.class::isInstance);
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent event) {
      EventHandler.cacheConfigs();
      ClassicBar.logger.info("Syncing Classic Bar Configs");
  }
}