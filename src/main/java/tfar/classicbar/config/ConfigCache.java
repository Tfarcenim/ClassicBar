package tfar.classicbar.config;

import tfar.classicbar.api.Color;

public class ConfigCache {

    public static Color hunger;
    public static Color hungerDebuff;
    public static Color saturation;
    public static Color saturationDebuff;
    public static Color thirst;
    public static Color thirstDebuff;
    public static Color hydration;
    public static Color hydrationDebuff;

    public static void bake() {
        hunger = Color.hex2Color(ClassicBarsConfig.hungerBarColor.get());
        hungerDebuff = Color.hex2Color(ClassicBarsConfig.hungerBarDebuffColor.get());
        saturation = Color.hex2Color(ClassicBarsConfig.saturationBarColor.get());
        saturationDebuff = Color.hex2Color(ClassicBarsConfig.saturationBarDebuffColor.get());
        thirst = Color.hex2Color(ClassicBarsConfig.thirstBarColor.get());
        thirstDebuff = Color.hex2Color(ClassicBarsConfig.thirstBarDebuffColor.get());
        hydration = Color.hex2Color(ClassicBarsConfig.hydrationBarColor.get());
        hydrationDebuff = Color.hex2Color(ClassicBarsConfig.hydrationBarDebuffColor.get());
    }
}
