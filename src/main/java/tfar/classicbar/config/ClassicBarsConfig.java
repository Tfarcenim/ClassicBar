package tfar.classicbar.config;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = ClassicBar.MODID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClassicBarsConfig {

  static ForgeConfigSpec.BooleanValue displayIcons;
  public static ForgeConfigSpec.BooleanValue displayToughnessBar;
  public static ForgeConfigSpec.BooleanValue fullAbsorptionBar;
  public static ForgeConfigSpec.BooleanValue fullArmorBar;
  public static ForgeConfigSpec.BooleanValue fullToughnessBar;
  public static ForgeConfigSpec.BooleanValue lowArmorWarning;
  public static ForgeConfigSpec.BooleanValue showSaturationBar;
  public static ForgeConfigSpec.BooleanValue showHeldFoodOverlay;
  public static ForgeConfigSpec.BooleanValue showExhaustionOverlay;
  public static ForgeConfigSpec.BooleanValue showAbsorptionNumbers;
  public static ForgeConfigSpec.BooleanValue showAirNumbers;
  public static ForgeConfigSpec.BooleanValue showArmorNumbers;
  public static ForgeConfigSpec.BooleanValue showArmorToughnessNumbers;
  public static ForgeConfigSpec.BooleanValue showHealthNumbers;
  public static ForgeConfigSpec.BooleanValue showHungerNumbers;
  public static ForgeConfigSpec.BooleanValue showMountHealthNumbers;

  public static ForgeConfigSpec.DoubleValue transitionSpeed;
  static ForgeConfigSpec.ConfigValue<String> hungerBarColor;
  static ForgeConfigSpec.ConfigValue<String> hungerBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<String> saturationBarColor;
  static ForgeConfigSpec.ConfigValue<String> saturationBarDebuffColor;
  static ForgeConfigSpec.ConfigValue<String> airBarColor;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> armorColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> armorToughnessColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionPoisonColors;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> absorptionWitherColors;
  public static ForgeConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> normalColors;
  public static ForgeConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> poisonedColors;
  public static ForgeConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
  static ForgeConfigSpec.ConfigValue<List<? extends String>> witheredColors;
  public static ForgeConfigSpec.ConfigValue<String> lavaBarColor;
  public static ForgeConfigSpec.ConfigValue<String> flightBarColor;

  public static ForgeConfigSpec.ConfigValue<List<? extends String>> leftorder;
  public static ForgeConfigSpec.ConfigValue<List<? extends String>> rightorder;

  public ClassicBarsConfig(ForgeConfigSpec.Builder builder) {
    builder.push("general");
    displayIcons = builder.define("display_icons", true);

    displayToughnessBar = builder.comment("Whether to show icons next to the bars").define("display_icons", true);
    fullAbsorptionBar = builder.define("full_absorption_bar", false);
    fullArmorBar = builder.define("full_armor_bar", false);
    fullToughnessBar = builder.define("full_toughness_bar", false);
    lowArmorWarning = builder.define("display_low_armor_warning", true);

    showAbsorptionNumbers = builder.define("show_absorption_numbers",true);
    showAirNumbers = builder.define("show_air_numbers",true);
    showArmorNumbers = builder.define("show_armor_numbers",true);
    showArmorToughnessNumbers = builder.define("show_armor_toughness_numbers",true);
    showHealthNumbers = builder.define("show_health_numbers",true);
    showHungerNumbers = builder.define("show_hunger_numbers",true);
    showMountHealthNumbers = builder.define("show_mount_health_numbers",true);

    showSaturationBar = builder.define("show_saturation_bar", true);
    showHeldFoodOverlay = builder.define("show_held_food_overlay", true);
    showExhaustionOverlay = builder.define("show_exhaustion_overlay", true);
    transitionSpeed = builder.defineInRange("transition_speed", 3, 0, Double.MAX_VALUE);

    hungerBarColor = builder.define("hunger_bar_color","#B34D00",String.class::isInstance);
    hungerBarDebuffColor = builder.define("hunger_bar_debuff_color","#249016",String.class::isInstance);
    airBarColor = builder.define("air_bar_color","#00E6E6",String.class::isInstance);
    saturationBarColor = builder.define("saturation_bar_color","#FFCC00",String.class::isInstance);
    saturationBarDebuffColor = builder.define("saturation_bar_debuff_color","#87BC00",String.class::isInstance);
    lavaBarColor = builder.define("lava_bar_color","#FF8000",String.class::isInstance);
    flightBarColor = builder.define("flight_bar_color","#FFFFFF",String.class::isInstance);

    armorColors = builder.defineList("armor_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),String.class::isInstance);
    armorToughnessColors = builder.defineList("armor_toughness_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"),String.class::isInstance);
    absorptionColors = builder.defineList("absorption_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);
    absorptionPoisonColors = builder.defineList("absorption_poison_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);
    absorptionWitherColors = builder.defineList("absorption_wither_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"),String.class::isInstance);

    normalColors = builder.defineList("normal_colors", Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"),String.class::isInstance);
    normalFractions = builder.defineList("normal_fractions", Lists.newArrayList(.25, .5, .75),Double.class::isInstance);
    poisonedColors = builder.defineList("poisoned_colors", Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"),String.class::isInstance);
    poisonedFractions = builder.defineList("poisoned_fractions", Lists.newArrayList(.25, .5, .75),Double.class::isInstance);
    witheredColors = builder.defineList("withered_colors", Lists.newArrayList("#555555", "#AAAAAA", "#555555"),String.class::isInstance);
    witheredFractions = builder.defineList("withered_fractions", Lists.newArrayList(.25, .5, .75),Double.class::isInstance);

    leftorder = builder.defineList("left_order", Lists.newArrayList("health","armor","absorption","lavacharm","lavacharm2"),String.class::isInstance);
    rightorder = builder.defineList("right_order", Lists.newArrayList("blood","health_mount","food","feathers","armor_toughness","thirst","air","flighttiara","decay"),String.class::isInstance);
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent event) {
      EventHandler.cacheConfigs();
      ClassicBar.logger.info("Syncing Classic Bar Configs");
  }
}