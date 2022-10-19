package tfar.classicbar.config;

import net.minecraftforge.common.ForgeConfigSpec;
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
    public static Color air;
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> normalFractions;
    public static List<Color> normal = new ArrayList<>();
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> poisonedFractions;
    public static List<Color> poison = new ArrayList<>();
    //public static ForgeConfigSpec.ConfigValue<List<? extends Double>> witheredFractions;
    public static List<Color> wither = new ArrayList<>();
    public static List<Color> absorption = new ArrayList<>();
    public static List<Color> absorptionPoison = new ArrayList<>();
    public static List<Color> absorptionWither = new ArrayList<>();
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
        air = ColorUtils.hex2Color(ClassicBarsConfig.airBarColor.get());
    }

    private static void cacheList(ForgeConfigSpec.ConfigValue<List<? extends String>> config, List<Color> cache) {
        for (String s : config.get()) {
            cache.add(ColorUtils.hex2Color(s));
        }
    }
}
