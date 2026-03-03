package tfar.classicbar.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.io.IOUtils;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.mod.StaminaB;
import tfar.classicbar.util.ModUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = ClassicBar.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClassicBarsConfig {

  static ModConfigSpec.BooleanValue displayIcons;
  public static ModConfigSpec.BooleanValue displayToughnessBar;
  public static ModConfigSpec.BooleanValue fullAbsorptionBar;
  public static ModConfigSpec.BooleanValue fullArmorBar;
  public static ModConfigSpec.BooleanValue fullToughnessBar;
  public static ModConfigSpec.BooleanValue lowArmorWarning;
  public static ModConfigSpec.BooleanValue showSaturationBar;
  public static ModConfigSpec.BooleanValue showHydrationBar;
  public static ModConfigSpec.BooleanValue showHeldFoodOverlay;
  public static ModConfigSpec.BooleanValue showHeldDrinkOverlay;
  public static ModConfigSpec.BooleanValue showExhaustionOverlay;
  public static ModConfigSpec.BooleanValue showThirstExhaustionOverlay;

  public static ModConfigSpec.DoubleValue transitionSpeed;
  static ModConfigSpec.ConfigValue<String> hungerBarColor;
  static ModConfigSpec.ConfigValue<String> hungerBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> saturationBarColor;
  static ModConfigSpec.ConfigValue<String> saturationBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> thirstBarColor;
  static ModConfigSpec.ConfigValue<String> thirstBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> hydrationBarColor;
  static ModConfigSpec.ConfigValue<String> hydrationBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> airBarColor;
  static ModConfigSpec.ConfigValue<List<? extends String>> armorColors;
  static ModConfigSpec.ConfigValue<List<? extends String>> armorToughnessColors;
  static ModConfigSpec.ConfigValue<List<? extends String>> absorptionColors;
  static ModConfigSpec.ConfigValue<List<? extends String>> absorptionPoisonColors;
  static ModConfigSpec.ConfigValue<List<? extends String>> absorptionWitherColors;
  public static ModConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
  static ModConfigSpec.ConfigValue<List<? extends String>> normalColors;
  public static ModConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
  static ModConfigSpec.ConfigValue<List<? extends String>> poisonedColors;
  public static ModConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
  static ModConfigSpec.ConfigValue<List<? extends String>> witheredColors;
  // Changed: replaced frozenColors (List<String>) + frozenFractions (List<Double>) with a single
  // frozenHealthColor string. Frozen state was simplified from a gradient to one flat color.
  public static ModConfigSpec.ConfigValue<String> frozenHealthColor;
  public static ModConfigSpec.ConfigValue<String> lavaBarColor;
  public static ModConfigSpec.ConfigValue<String> flightBarColor;

  public static ModConfigSpec.ConfigValue<List<? extends String>> leftorder;
  public static ModConfigSpec.ConfigValue<List<? extends String>> rightorder;

  public ClassicBarsConfig(ModConfigSpec.Builder builder) {
    builder.push("general");
    displayIcons = builder.define("display_icons", true);

    displayToughnessBar = builder.comment("Whether to show icons next to the bars").define("display_icons", true);
    fullAbsorptionBar = builder.define("full_absorption_bar", false);
    fullArmorBar = builder.define("full_armor_bar", false);
    fullToughnessBar = builder.define("full_toughness_bar", false);
    lowArmorWarning = builder.define("display_low_armor_warning", true);

    showSaturationBar = builder.define("show_saturation_bar", true);
    showHydrationBar = builder.define("show_hydration_bar", true);
    showHeldFoodOverlay = builder.define("show_held_food_overlay", true);
    showHeldDrinkOverlay = builder.define("show_held_drink_overlay", true);
    showExhaustionOverlay = builder.define("show_exhaustion_overlay", true);
    showThirstExhaustionOverlay = builder.define("show_thirst_exhaustion_overlay", true);
    transitionSpeed = builder.defineInRange("transition_speed", 3, 0, Double.MAX_VALUE);

    hungerBarColor = builder.define("hunger_bar_color","#B34D00",String.class::isInstance);
    hungerBarDebuffColor = builder.define("hunger_bar_debuff_color","#249016",String.class::isInstance);
    thirstBarColor = builder.define("thirstr_bar_color","#1C5EE4",String.class::isInstance);
    thirstBarDebuffColor = builder.define("thirst_bar_debuff_color","#5A891C",String.class::isInstance);
    airBarColor = builder.define("air_bar_color","#00E6E6",String.class::isInstance);
    saturationBarColor = builder.define("saturation_bar_color","#FFCC00",String.class::isInstance);
    saturationBarDebuffColor = builder.define("saturation_bar_debuff_color","#87BC00",String.class::isInstance);
    hydrationBarColor = builder.define("hydration_bar_color","#00A3E2",String.class::isInstance);
    hydrationBarDebuffColor = builder.define("hydration_bar_debuff_color","#85CF25",String.class::isInstance);
    lavaBarColor = builder.define("lava_bar_color","#FF8000",String.class::isInstance);
    flightBarColor = builder.define("flight_bar_color","#FFFFFF",String.class::isInstance);

    armorColors = builder.defineList("armor_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), () -> "", String.class::isInstance);
    armorToughnessColors = builder.defineList("armor_toughness_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), () -> "", String.class::isInstance);
    absorptionColors = builder.defineList("absorption_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);
    absorptionPoisonColors = builder.defineList("absorption_poison_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);
    absorptionWitherColors = builder.defineList("absorption_wither_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);

    normalColors = builder.defineList("normal_colors", Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"), () -> "", String.class::isInstance);
    normalFractions = builder.defineList("normal_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    poisonedColors = builder.defineList("poisoned_colors", Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"), () -> "", String.class::isInstance);
    poisonedFractions = builder.defineList("poisoned_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    witheredColors = builder.defineList("withered_colors", Lists.newArrayList("#555555", "#AAAAAA", "#555555"), () -> "", String.class::isInstance);
    witheredFractions = builder.defineList("withered_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    frozenHealthColor = builder.define("frozen_health_color", "#7fafff"); // Changed: was frozenColors list + frozenFractions list

    leftorder = builder.defineList("left_order", Lists.newArrayList("health","armor","absorption","lavacharm","lavacharm2"), () -> "", String.class::isInstance);
    rightorder = builder.defineList("right_order", Lists.newArrayList("blood","health_mount","food","thirst_level", StaminaB.name,"feathers","armor_toughness","air","flighttiara","decay"), () -> "", String.class::isInstance);
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent event) {
      EventHandler.cacheConfigs();
      readBarSettings();
      ClassicBar.logger.info("Syncing Classic Bar Configs");
  }

  static File settingsPath = new File("config/" + ClassicBar.MODID + "/");

  // Changed: new method; reads per-bar JSON files from config/classicbar/ and applies
  // BarSettings (show_text, icon) to each registered overlay. Replaces the old approach
  // where each overlay class hardcoded its shouldRenderText() and getIconRL() overrides.
  public static void readBarSettings() {

    if (!settingsPath.exists()) {
      settingsPath.mkdir();
    }
    writeDefault();

    File[] files = settingsPath.listFiles();
    if (files == null) return;

    for (File file : files) {
      if (!file.isFile() || !file.getName().endsWith(".json")) continue;

      Reader reader = null;
      try {
        reader = new FileReader(file);

        JsonReader jsonReader = new JsonReader(reader);

        Gson gson = new GsonBuilder().registerTypeAdapter(ResourceLocation.class,new ResourceLocation.Serializer()).create();

        BarSettings barSettings = gson.fromJson(jsonReader, BarSettings.class);
        String fileName = file.getName();
        String name = fileName.substring(0,fileName.length() - ".json".length());
        BarOverlay barOverlay = EventHandler.registry.get(name);
        if (barOverlay == null) {
          barOverlay = EventHandler.registry.get(name.replace('_', ':'));
        }
        if (barOverlay != null) {
          barOverlay.setBarSettings(barSettings);
        }

      } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(reader);
      }
    }
  }

  /*public static class BarSettingsDeserializer implements JsonDeserializer<BarSettings> {

    @Override
    public BarSettings deserialize
            (JsonElement jElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
      JsonObject jObject = jElement.getAsJsonObject();
      int intValue = jObject.get("valueInt").getAsInt();
      String stringValue = jObject.get("valueString").getAsString();
      return new BarSettings(intValue, stringValue);
    }
  }*/

  // Changed: new method; writes a default JSON settings file for each registered overlay
  // only when the file does not already exist, so user edits are preserved.
  public static void writeDefault() {
    Gson gson = new Gson();
    makeDefaultBarSettings();
    for (BarOverlay barOverlay : EventHandler.registry.values()) {
      // Changed: colon replaced with underscore in filenames (e.g. "parcool:stamina" -> "parcool_stamina.json")
      File file = new File("config/" + ClassicBar.MODID + "/"+ barOverlay.name().replace(':', '_')+".json");
      if (file.exists()) continue; // Changed: skip existing files so user edits are not overwritten
      JsonWriter writer = null;
      try {
        writer = gson.newJsonWriter(new FileWriter(file));
        writer.setIndent("    ");

        BarSettings barSettings = defaults.getOrDefault(barOverlay.name(),nullSettings);
        gson.toJson(barSettings.toJson(), writer);

      } catch (Exception e) {
        ClassicBar.logger.error("Couldn't save config");
        e.printStackTrace();
        throw new RuntimeException(e);
      } finally {
        IOUtils.closeQuietly(writer);
      }
    }
  }

  private static final BarSettings nullSettings = new BarSettings();

  // Changed: new method; defines the default BarSettings for each overlay.
  // Previously each overlay class overrode getIconRL() and shouldRenderText() directly.
  // Now icon and show_text defaults are centralized here and written to JSON on first load.
  static void makeDefaultBarSettings() {

    nullSettings.show_text = true;
    nullSettings.icon = BarOverlayImpl.GUI_ICONS_LOCATION;

    BarSettings absorbSettings = nullSettings.copy();
    defaults.put("absorption",absorbSettings);

    BarSettings airSettings = nullSettings.copy();
    defaults.put("air",airSettings);

    BarSettings armorSettings = nullSettings.copy();
    defaults.put("armor",armorSettings);

    BarSettings armorToughnessSettings = nullSettings.copy();
    armorToughnessSettings.icon = BarOverlayImpl.ICON_BAR;
    defaults.put("armor_toughness",armorToughnessSettings);

    BarSettings bloodSettings = nullSettings.copy();
    bloodSettings.icon = ModUtils.VAMPIRISM_ICONS;
    defaults.put("blood",bloodSettings);

    BarSettings foodSettings = nullSettings.copy();
    defaults.put("food",foodSettings);

    BarSettings healthSettings = nullSettings.copy();
    defaults.put("health",healthSettings);

    BarSettings healthMountSettings = nullSettings.copy();
    defaults.put("health_mount",healthMountSettings);

    BarSettings thirstSettings = nullSettings.copy();
    thirstSettings.icon = ModUtils.THIRST_ICON;
    defaults.put("thirst_level",thirstSettings);

    BarSettings staminaSettings = nullSettings.copy();
    staminaSettings.icon = ModUtils.ICONS;
    defaults.put(StaminaB.name,staminaSettings);
  }



  private static final Map<String,BarSettings> defaults = new HashMap<>();
}