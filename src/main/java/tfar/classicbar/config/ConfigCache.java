package tfar.classicbar.config;

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
    private static void clear() {
        armor.clear();
    }
    public static void bake() {
        clear();
        icons = ClassicBarsConfig.displayIcons.get();
        for (String s : ClassicBarsConfig.armorColorValues.get()) {
            armor.add(ColorUtils.hex2Color(s));
        }

        for (String s : ClassicBarsConfig.armorToughnessColorValues.get()) {
            armor_toughness.add(ColorUtils.hex2Color(s));
        }

        hunger = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarColor.get());
        hungerDebuff = ColorUtils.hex2Color(ClassicBarsConfig.hungerBarDebuffColor.get());
        saturation = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarColor.get());
        saturationDebuff = ColorUtils.hex2Color(ClassicBarsConfig.saturationBarDebuffColor.get());

    }
}
