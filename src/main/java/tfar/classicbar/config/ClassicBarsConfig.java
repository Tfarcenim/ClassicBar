package tfar.classicbar.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList; // Added: used to check at config-build time whether mod-specific sections should be registered
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.mod.HomeostaticWater;
import tfar.classicbar.impl.overlays.mod.IronsMana;
import tfar.classicbar.impl.overlays.mod.StaminaB;
import tfar.classicbar.impl.overlays.mod.ThirstWasTaken;
import tfar.classicbar.util.ModUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("removal") // Suppressed: ModConfigSpec.Builder methods are marked for removal but no replacement exists yet in NeoForge 1.21
@EventBusSubscriber(modid = ClassicBar.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClassicBarsConfig {

  static ModConfigSpec.BooleanValue displayIcons;
  public static ModConfigSpec.BooleanValue displayToughnessBar;
  public static ModConfigSpec.BooleanValue fullAbsorptionBar;
  public static ModConfigSpec.BooleanValue fullArmorBar;
  public static ModConfigSpec.BooleanValue fullToughnessBar;
  public static ModConfigSpec.BooleanValue lowArmorWarning;
  public static ModConfigSpec.BooleanValue showSaturationBar;
  // Added: null when neither toughasnails nor homeostatic is loaded — always check before calling .get()
  public static ModConfigSpec.BooleanValue showHydrationBar;
  public static ModConfigSpec.BooleanValue showHeldFoodOverlay;
  // Added: null when toughasnails is not loaded — only registered inside the toughasnails config section
  public static ModConfigSpec.BooleanValue showHeldDrinkOverlay;
  public static ModConfigSpec.BooleanValue showExhaustionOverlay;
  // Added: null when toughasnails is not loaded — only registered inside the toughasnails config section
  public static ModConfigSpec.BooleanValue showThirstExhaustionOverlay;

  public static ModConfigSpec.DoubleValue transitionSpeed;
  static ModConfigSpec.ConfigValue<String> hungerBarColor;
  static ModConfigSpec.ConfigValue<String> hungerBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> saturationBarColor;
  static ModConfigSpec.ConfigValue<String> saturationBarDebuffColor;
  // Added: null when toughasnails is not loaded — registered only inside the toughasnails config section
  static ModConfigSpec.ConfigValue<String> thirstBarColor;
  static ModConfigSpec.ConfigValue<String> thirstBarDebuffColor;
  static ModConfigSpec.ConfigValue<String> hydrationBarColor;
  static ModConfigSpec.ConfigValue<String> hydrationBarDebuffColor;
  // Added: null when the "thirst" (Thirst Was Taken) mod is not loaded — registered only inside the thirst_was_taken section
  static ModConfigSpec.ConfigValue<String> thirstWasTakenBarColor;
  static ModConfigSpec.ConfigValue<String> thirstWasTakenQuenchedBarColor;
  // Added: null when homeostatic is not loaded — registered only inside the homeostatic config section
  static ModConfigSpec.ConfigValue<String> homeostaticWaterBarColor;
  static ModConfigSpec.ConfigValue<String> homeostaticHydrationBarColor;
  // Added: null when irons_spellbooks is not loaded — registered only inside the irons_spellbooks config section
  static ModConfigSpec.ConfigValue<String> ironsManaBarColor;
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
  public static ModConfigSpec.ConfigValue<String> frozenHealthColor;
  public static ModConfigSpec.ConfigValue<String> lavaBarColor;
  public static ModConfigSpec.ConfigValue<String> flightBarColor;

  public static ModConfigSpec.ConfigValue<List<? extends String>> leftorder;
  public static ModConfigSpec.ConfigValue<List<? extends String>> rightorder;

  public ClassicBarsConfig(ModConfigSpec.Builder builder) {
    // Added: capture mod presence at config-build time to gate which TOML sections are registered;
    // config sections for absent mods are skipped entirely so they don't appear in the config screen
    boolean tanLoaded = ModList.get().isLoaded("toughasnails"); // Tough as Nails mod id
    boolean thirstLoaded = ModList.get().isLoaded("thirst");    // Thirst Was Taken mod id
    boolean homeostaticLoaded = ModList.get().isLoaded("homeostatic"); // Homeostatic mod id
    boolean ironsSpellbooksLoaded = ModList.get().isLoaded("irons_spellbooks"); // Iron's Spells n Spellbooks mod id

    builder.push("general"); // Added: explicit section push — must be paired with builder.pop() below so mod sections can be pushed at the same level
    // Added: .translation() calls on each value link the config key to en_us.json strings so NeoForge's ConfigurationScreen shows localized labels
    displayIcons = builder.comment("Whether to show icons next to the bars").translation("classicbar.config.general.display_icons").define("display_icons", true);

    displayToughnessBar = builder.comment("Whether to display the armor toughness bar").translation("classicbar.config.general.display_toughness_bar").define("display_toughness_bar", true);
    fullAbsorptionBar = builder.translation("classicbar.config.general.full_absorption_bar").define("full_absorption_bar", false);
    fullArmorBar = builder.translation("classicbar.config.general.full_armor_bar").define("full_armor_bar", false);
    fullToughnessBar = builder.translation("classicbar.config.general.full_toughness_bar").define("full_toughness_bar", false);
    lowArmorWarning = builder.translation("classicbar.config.general.display_low_armor_warning").define("display_low_armor_warning", true);

    showSaturationBar = builder.translation("classicbar.config.general.show_saturation_bar").define("show_saturation_bar", true);
    showHeldFoodOverlay = builder.translation("classicbar.config.general.show_held_food_overlay").define("show_held_food_overlay", true);
    showExhaustionOverlay = builder.translation("classicbar.config.general.show_exhaustion_overlay").define("show_exhaustion_overlay", true);
    transitionSpeed = builder.translation("classicbar.config.general.transition_speed").defineInRange("transition_speed", 3, 0, Double.MAX_VALUE);

    hungerBarColor = builder.translation("classicbar.config.general.hunger_bar_color").define("hunger_bar_color","#B34D00",String.class::isInstance);
    hungerBarDebuffColor = builder.translation("classicbar.config.general.hunger_bar_debuff_color").define("hunger_bar_debuff_color","#249016",String.class::isInstance);
    airBarColor = builder.translation("classicbar.config.general.air_bar_color").define("air_bar_color","#00E6E6",String.class::isInstance);
    saturationBarColor = builder.translation("classicbar.config.general.saturation_bar_color").define("saturation_bar_color","#FFCC00",String.class::isInstance);
    saturationBarDebuffColor = builder.translation("classicbar.config.general.saturation_bar_debuff_color").define("saturation_bar_debuff_color","#87BC00",String.class::isInstance);
    lavaBarColor = builder.translation("classicbar.config.general.lava_bar_color").define("lava_bar_color","#FF8000",String.class::isInstance);
    flightBarColor = builder.translation("classicbar.config.general.flight_bar_color").define("flight_bar_color","#FFFFFF",String.class::isInstance);

    armorColors = builder.translation("classicbar.config.general.armor_color_values").defineList("armor_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), () -> "", String.class::isInstance);
    armorToughnessColors = builder.translation("classicbar.config.general.armor_toughness_color_values").defineList("armor_toughness_color_values", Lists.newArrayList("#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"), () -> "", String.class::isInstance);
    absorptionColors = builder.translation("classicbar.config.general.absorption_color_values").defineList("absorption_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);
    absorptionPoisonColors = builder.translation("classicbar.config.general.absorption_poison_color_values").defineList("absorption_poison_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);
    absorptionWitherColors = builder.translation("classicbar.config.general.absorption_wither_color_values").defineList("absorption_wither_color_values", Lists.newArrayList("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4", "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF"), () -> "", String.class::isInstance);

    normalColors = builder.translation("classicbar.config.general.normal_colors").defineList("normal_colors", Lists.newArrayList("#FF0000", "#FFFF00", "#00FF00"), () -> "", String.class::isInstance);
    normalFractions = builder.translation("classicbar.config.general.normal_fractions").defineList("normal_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    poisonedColors = builder.translation("classicbar.config.general.poisoned_colors").defineList("poisoned_colors", Lists.newArrayList("#00FF00", "#55FF55", "#00FF00"), () -> "", String.class::isInstance);
    poisonedFractions = builder.translation("classicbar.config.general.poisoned_fractions").defineList("poisoned_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    witheredColors = builder.translation("classicbar.config.general.withered_colors").defineList("withered_colors", Lists.newArrayList("#555555", "#AAAAAA", "#555555"), () -> "", String.class::isInstance);
    witheredFractions = builder.translation("classicbar.config.general.withered_fractions").defineList("withered_fractions", Lists.newArrayList(.25, .5, .75), () -> 0.0, Double.class::isInstance);
    frozenHealthColor = builder.translation("classicbar.config.general.frozen_health_color").define("frozen_health_color", "#7fafff"); // Changed: was frozenColors list + frozenFractions list

    leftorder = builder.translation("classicbar.config.general.left_order").defineList("left_order", Lists.newArrayList("health","armor","absorption","lavacharm","lavacharm2"), () -> "", String.class::isInstance);
    rightorder = builder.translation("classicbar.config.general.right_order").defineList("right_order", Lists.newArrayList("blood","health_mount","food","thirst_level", StaminaB.name,"feathers","armor_toughness","air","flighttiara","decay"), () -> "", String.class::isInstance);
    builder.pop(); // Added: closes the "general" push above; required before pushing mod-specific sections at the top level

    // Tough as Nails section: only registered when toughasnails is loaded
    if (tanLoaded) {
      builder.push("toughasnails");
      // showHydrationBar is shared with Homeostatic; defined here when TAN is present
      showHydrationBar = builder.translation("classicbar.config.toughasnails.show_hydration_bar").define("show_hydration_bar", true);
      showHeldDrinkOverlay = builder.translation("classicbar.config.toughasnails.show_held_drink_overlay").define("show_held_drink_overlay", true);
      showThirstExhaustionOverlay = builder.translation("classicbar.config.toughasnails.show_thirst_exhaustion_overlay").define("show_thirst_exhaustion_overlay", true);
      thirstBarColor = builder.translation("classicbar.config.toughasnails.thirst_bar_color").define("thirst_bar_color","#1C5EE4",String.class::isInstance);
      thirstBarDebuffColor = builder.translation("classicbar.config.toughasnails.thirst_bar_debuff_color").define("thirst_bar_debuff_color","#5A891C",String.class::isInstance);
      hydrationBarColor = builder.translation("classicbar.config.toughasnails.hydration_bar_color").define("hydration_bar_color","#00A3E2",String.class::isInstance);
      hydrationBarDebuffColor = builder.translation("classicbar.config.toughasnails.hydration_bar_debuff_color").define("hydration_bar_debuff_color","#85CF25",String.class::isInstance);
      builder.pop(); // End toughasnails section
    }

    // Thirst Was Taken section: only registered when the "thirst" mod is loaded
    if (thirstLoaded) {
      builder.push("thirst_was_taken");
      thirstWasTakenBarColor = builder.translation("classicbar.config.thirst_was_taken.thirst_was_taken_bar_color").define("thirst_was_taken_bar_color","#1C5EE4",String.class::isInstance);
      thirstWasTakenQuenchedBarColor = builder.translation("classicbar.config.thirst_was_taken.thirst_was_taken_quenched_bar_color").define("thirst_was_taken_quenched_bar_color","#00A3E2",String.class::isInstance);
      builder.pop(); // End thirst_was_taken section
    }

    // Homeostatic section: only registered when homeostatic is loaded
    if (homeostaticLoaded) {
      builder.push("homeostatic");
      homeostaticWaterBarColor = builder.translation("classicbar.config.homeostatic.homeostatic_water_bar_color").define("homeostatic_water_bar_color","#1C5EE4",String.class::isInstance);
      homeostaticHydrationBarColor = builder.translation("classicbar.config.homeostatic.homeostatic_hydration_bar_color").define("homeostatic_hydration_bar_color","#00A3E2",String.class::isInstance);
      if (!tanLoaded) {
        // showHydrationBar shared with TAN; define here only when TAN is absent
        showHydrationBar = builder.translation("classicbar.config.homeostatic.show_hydration_bar").define("show_hydration_bar", true);
      }
      builder.pop(); // End homeostatic section
    }

    // Iron's Spells n Spellbooks section: only registered when irons_spellbooks is loaded
    if (ironsSpellbooksLoaded) {
      builder.push("irons_spellbooks");
      ironsManaBarColor = builder.translation("classicbar.config.irons_spellbooks.irons_mana_bar_color").define("irons_mana_bar_color", "#1CAAE6", String.class::isInstance); // Mana-blue default
      builder.pop(); // End irons_spellbooks section
    }
  }

  @SubscribeEvent
  public static void onConfigChanged(ModConfigEvent event) {
      EventHandler.cacheConfigs();
      readBarSettings();
      ClassicBar.logger().info("Syncing Classic Bar Configs");
  }

  static File settingsPath = new File("config/" + ClassicBar.MODID + "/");

  // Changed: new method; reads per-bar JSON files from config/classicbar/ and applies
  // BarSettings (show_text, icon) to each registered overlay. Replaces the old approach
  // where each overlay class hardcoded its shouldRenderText() and getIconRL() overrides.
  // Now icon and show_text defaults are centralized here and written to JSON on first load.
  public static void readBarSettings() {

    if (!settingsPath.exists()) {
      settingsPath.mkdirs(); // Fix: mkdirs() ensures parent directories (e.g. config/) are also created
    }
    writeDefault();

    File[] files = settingsPath.listFiles();
    if (files == null) return;

    for (File file : files) {
      if (!file.isFile() || !file.getName().endsWith(".json")) continue;

      // NeoForge 1.21: use try-with-resources for Closeable resources (§17)
      try (Reader reader = new FileReader(file)) {
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
        ClassicBar.logger().error("Failed to read bar settings from {}, skipping", file.getName(), e);
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
      // NeoForge 1.21: use try-with-resources for Closeable resources (§17)
      try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
        writer.setIndent("    ");

        BarSettings barSettings = defaults.getOrDefault(barOverlay.name(),nullSettings);
        gson.toJson(barSettings.toJson(), writer);

      } catch (Exception e) {
        ClassicBar.logger().error("Couldn't save default config for {}, skipping", barOverlay.name(), e);
      }
    }
  }

  private static final BarSettings nullSettings = new BarSettings();

  // Changed: new method; defines the default BarSettings for each overlay.
  // Previously each overlay class overrode getIconRL() and shouldRenderText() directly.
  // Now icon and show_text defaults are centralized here and written to JSON on first load.
  static void makeDefaultBarSettings() {
    if (!defaults.isEmpty()) return; // Fix: only build defaults once — avoids redundant allocations on every config reload

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

    BarSettings thirstWasTakenSettings = nullSettings.copy();
    thirstWasTakenSettings.icon = ModUtils.THIRST_WAS_TAKEN_ICONS;
    defaults.put(ThirstWasTaken.NAME, thirstWasTakenSettings);

    BarSettings homeostaticWaterSettings = nullSettings.copy();
    homeostaticWaterSettings.icon = ModUtils.HOMEOSTATIC_ICONS;
    defaults.put(HomeostaticWater.NAME, homeostaticWaterSettings);

    BarSettings ironsManaSettings = nullSettings.copy();
    ironsManaSettings.icon = ModUtils.IRONS_SPELLBOOKS_ICONS;
    defaults.put(IronsMana.NAME, ironsManaSettings);
  }



  private static final Map<String,BarSettings> defaults = new HashMap<>();
}