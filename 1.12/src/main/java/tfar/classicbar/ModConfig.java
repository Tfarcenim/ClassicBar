package tfar.classicbar;

import net.minecraftforge.common.config.Config;

@Config(modid = ClassicBar.MODID)
public class ModConfig {

    @Config.Name("Armor bar colors")
    @Config.Comment("Colors must be specified in #RRGGBB format")
    public static String[] armorColorValues = new String[]{"#AAAAAA","#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};

    @Config.Name("Show Icons")
    @Config.Comment("Whether to show icons next to the bars")
    public static boolean displayIcons = true;

    @Config.Name("Display Armor Toughness Bar")
    public static boolean displayToughnessBar = true;

    @Config.Name("Draw full absorption Bar")
    public static boolean fullAbsorptionBar = false;

    @Config.Name("Show numbers")
    public static boolean showNumbers = true;



}
