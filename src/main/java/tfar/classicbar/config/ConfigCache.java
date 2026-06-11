package tfar.classicbar.config;

import net.minecraftforge.common.ForgeConfigSpec;
import tfar.classicbar.api.Color;

import java.util.ArrayList;
import java.util.List;

public class ConfigCache {

    public static Color hunger;
    public static Color hungerDebuff;
    public static Color saturation;
    public static Color saturationDebuff;
    public static Color thirst;
    public static Color thirstDebuff;
    public static Color hydration;
    public static Color hydrationDebuff;
    public static List<Color> absorption = new ArrayList<>();
    public static List<Color> absorptionPoison = new ArrayList<>();
    public static List<Color> absorptionWither = new ArrayList<>();
    private static void clear() {
        absorption.clear();
        absorptionPoison.clear();
        absorptionWither.clear();
    }
    public static void bake() {
        clear();

        cacheList(ClassicBarsConfig.absorptionColors,absorption);
        cacheList(ClassicBarsConfig.absorptionPoisonColors,absorptionPoison);
        cacheList(ClassicBarsConfig.absorptionWitherColors,absorptionWither);
        hunger = Color.hex2Color(ClassicBarsConfig.hungerBarColor.get());
        hungerDebuff = Color.hex2Color(ClassicBarsConfig.hungerBarDebuffColor.get());
        saturation = Color.hex2Color(ClassicBarsConfig.saturationBarColor.get());
        saturationDebuff = Color.hex2Color(ClassicBarsConfig.saturationBarDebuffColor.get());
        thirst = Color.hex2Color(ClassicBarsConfig.thirstBarColor.get());
        thirstDebuff = Color.hex2Color(ClassicBarsConfig.thirstBarDebuffColor.get());
        hydration = Color.hex2Color(ClassicBarsConfig.hydrationBarColor.get());
        hydrationDebuff = Color.hex2Color(ClassicBarsConfig.hydrationBarDebuffColor.get());
    }

    private static void cacheList(ForgeConfigSpec.ConfigValue<List<? extends String>> config, List<Color> cache) {
        for (String s : config.get()) {
            cache.add(Color.hex2Color(s));
        }
    }
}
