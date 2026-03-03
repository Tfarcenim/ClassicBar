package tfar.classicbar.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class ConfigCache {

    public static boolean icons;
    public static List<Color> armor = new ArrayList<>();
    public static List<Color> armor_toughness = new ArrayList<>();
    public static Color hunger;
    public static Color hungerDebuff;
    public static Color saturation;
    public static Color saturationDebuff;
    public static Color thirst;
    public static Color thirstDebuff;
    public static Color hydration;
    public static Color hydrationDebuff;
    public static Color thirstWasTaken;
    public static Color thirstWasTakenQuenched;
    public static Color homeostaticWater;
    public static Color homeostaticHydration;
    public static Color air;
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
    public static List<Color> normal = new ArrayList<>();
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
    public static List<Color> poison = new ArrayList<>();
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
    public static List<Color> wither = new ArrayList<>();
    // Changed: replaced List<Color> frozen + cacheList with a single Color frozenHealth.
    // Frozen state no longer needs a multi-stop gradient; one config color is sufficient.
    // frozenColors list and frozenFractions config were removed from ClassicBarsConfig.
    public static List<Color> absorption = new ArrayList<>();
    public static List<Color> absorptionPoison = new ArrayList<>();
    public static List<Color> absorptionWither = new ArrayList<>();
    public static Color frozenHealth;
    private static void clear() {
        armor.clear();
        armor_toughness.clear();
        normal.clear();
        poison.clear();
        wither.clear();
        absorption.clear();
        absorptionPoison.clear();
        absorptionWither.clear();
    }
    public static void bake() {
        clear();
        icons = ClassicBarsConfig.displayIcons.get();

        cacheList(ClassicBarsConfig.armorColors,armor);
        cacheList(ClassicBarsConfig.armorToughnessColors,armor_toughness);
        cacheList(ClassicBarsConfig.normalColors,normal);
        cacheList(ClassicBarsConfig.poisonedColors,poison);
        cacheList(ClassicBarsConfig.witheredColors,wither);
        cacheList(ClassicBarsConfig.absorptionColors,absorption);
        cacheList(ClassicBarsConfig.absorptionPoisonColors,absorptionPoison);
        cacheList(ClassicBarsConfig.absorptionWitherColors,absorptionWither);
        hunger = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarColor.get());
        hungerDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarDebuffColor.get());
        saturation = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarColor.get());
        saturationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarDebuffColor.get());
        // Added: guard against null — these config fields are only registered (non-null) when their mod is present at launch
        if (ClassicBarsConfig.thirstBarColor != null) { // toughasnails section was registered
            thirst = ColorUtils.hex2Color(ClassicBarsConfig.thirstBarColor.get());
            thirstDebuff = ColorUtils.hex2Color(ClassicBarsConfig.thirstBarDebuffColor.get());
            hydration = ColorUtils.hex2Color(ClassicBarsConfig.hydrationBarColor.get());
            hydrationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hydrationBarDebuffColor.get());
        }
        if (ClassicBarsConfig.thirstWasTakenBarColor != null) { // thirst_was_taken section was registered
            thirstWasTaken = ColorUtils.hex2Color(ClassicBarsConfig.thirstWasTakenBarColor.get());
            thirstWasTakenQuenched = ColorUtils.hex2Color(ClassicBarsConfig.thirstWasTakenQuenchedBarColor.get());
        }
        if (ClassicBarsConfig.homeostaticWaterBarColor != null) { // homeostatic section was registered
            homeostaticWater = ColorUtils.hex2Color(ClassicBarsConfig.homeostaticWaterBarColor.get());
            homeostaticHydration = ColorUtils.hex2Color(ClassicBarsConfig.homeostaticHydrationBarColor.get());
        }
        air = ColorUtils.hex2Color(ClassicBarsConfig.airBarColor.get());
        frozenHealth = ColorUtils.hex2Color(ClassicBarsConfig.frozenHealthColor.get()); // Changed: was cacheList(frozenColors, frozen); simplified to one hex color
    }

    private static void cacheList(ModConfigSpec.ConfigValue<List<? extends String>> config, List<Color> cache) {
        for (String s : config.get()) {
            cache.add(ColorUtils.hex2Color(s));
        }
    }
}
