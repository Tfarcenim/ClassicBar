package tfar.classicbar.config;

import tfar.classicbar.api.Color;

public class ConfigCache {

    public static Color thirst;
    public static Color thirstDebuff;
    public static Color hydration;
    public static Color hydrationDebuff;

    public static void bake() {
        thirst = Color.hex2Color(ClassicBarsConfig.thirstBarColor.get());
        thirstDebuff = Color.hex2Color(ClassicBarsConfig.thirstBarDebuffColor.get());
        hydration = Color.hex2Color(ClassicBarsConfig.hydrationBarColor.get());
        hydrationDebuff = Color.hex2Color(ClassicBarsConfig.hydrationBarDebuffColor.get());
    }
}
