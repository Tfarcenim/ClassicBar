package tfar.classicbar.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.ClassicBar;

import static tfar.classicbar.ClassicBar.*;

@Config(modid = ClassicBar.MODID)
public class ModConfig {

    @Config.Comment({"General Options"})
    public static ConfigGeneral general = new ConfigGeneral();

    @Config.Comment({"Color Options"})
    public static ConfigColors colors = new ConfigColors();

    @Config.Comment({"Number Options"})
    public static ConfigNumbers numbers = new ConfigNumbers();

    @Config.Comment({"Warnings"})
    public static ConfigWarnings warnings = new ConfigWarnings();

    public static class ConfigGeneral {

        @Config.Name("Show Icons")
        @Config.Comment("Whether to show icons next to the bars")
        public boolean displayIcons = true;

        @Config.Name("Display Armor Toughness Bar")
        @Config.Comment("REQUIRES A RESTART TO APPLY!")
        public boolean displayToughnessBar = true;

        @Config.Name("Draw full absorption Bar")
        public boolean fullAbsorptionBar = false;

        @Config.Name("Show Saturation Bar")
        public boolean showSaturationBar = true;

        @Config.Name("Show Exhaustion Overlay")
        public boolean showExhaustionOverlay = true;

    }

    public static class ConfigNumbers {
        @Config.Name("Percentage based")
        public boolean showPercent = false;

        @Config.Comment("Numbers info")

        @Config.Name("Show Numbers")
        public boolean showNumbers = true;
    }

    public static class ConfigColors {
        @Config.Name("Advanced Options")
        public AdvancedColors advancedColors = new AdvancedColors();
        @Config.Name("Hunger Bar Color")
        public String hungerBarColor = "#B34D00";
        @Config.Name("Oxygen Bar Color")
        public String oxygenBarColor = "#00E6E6";
        @Config.Name("Saturation Bar Color")
        public String saturationBarColor = "#FFCC00";
        @Config.Name("Lava Bar Color")
        public String lavaBarColor = "#FF8000";
        @Config.Name("Thirst Bar Color")
        public String thirstBarColor = "#1C5EE4";

        public class AdvancedColors {
            @Config.Comment("Colors must be specified in #RRGGBB format")
            @Config.Name("Armor color values")
            public String[] armorColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};
            @Config.Name("Health fractions")
            public Float[] healthFractions = new Float[]{.25f, .5f, .75f};
            @Config.Name("Colors")
            public String[] hexColors = new String[]{"#FF0000", "#FFFF00", "#00FF00"};
        }
    }

    public static class ConfigWarnings {
        @Config.Name("Show Advanced Rocketry warning")
        @Config.Comment("Warning when advanced rocketry is installed")
        public boolean advancedRocketryWarning = true;
    }

    @Mod.EventBusSubscriber(modid = ClassicBar.MODID)
    public static class ConfigEventHandler {

        IdiotHandler idiots = new IdiotHandler();

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ClassicBar.MODID)) {
                ConfigManager.sync(ClassicBar.MODID, Config.Type.INSTANCE);
                idiots.idiotsTryingToParseBadHexColorsDOTJpeg();
                idiots.emptyArrayFixer();
                System.out.println("Syncing Classic Bar Configs");
            }
        }
    }
}