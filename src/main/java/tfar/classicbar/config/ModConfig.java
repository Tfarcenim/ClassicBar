package tfar.classicbar.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.ModUtils;

import static tfar.classicbar.config.IdiotHandler.idiots;

@Config(modid = ClassicBar.MODID)
public class ModConfig {

    @Config.Comment("General Options")
    public static ConfigGeneral general = new ConfigGeneral();

    @Config.Comment("Color Options")
    public static ConfigColors colors = new ConfigColors();

    @Config.Comment("Number Options")
    public static ConfigNumbers numbers = new ConfigNumbers();

    @Config.Comment("Warnings")
    public static ConfigWarnings warnings = new ConfigWarnings();

    @Config.Comment("Mod Options")
    public static ConfigMods mods = new ConfigMods();

    public static class ConfigGeneral {
        @Config.Name("Bar Overlays")
        @Config.Comment("Tweak the bars themselves")
        public BarOverlays overlays = new BarOverlays();
        @Config.Name("Show Icons")
        @Config.Comment("Whether to show icons next to the bars")
        public boolean displayIcons = true;
        public class BarOverlays {

            @Config.Name("Hunger Bar Overlays")
            public HungerBarConfig hunger = new HungerBarConfig();

            @Config.Name("Display Armor Toughness Bar")
            public boolean displayToughnessBar = true;

            @Config.Name("Draw full absorption Bar")
            public boolean fullAbsorptionBar = false;

            @Config.Name("Draw full armor Bar")
            public boolean fullArmorBar = false;

            @Config.Name("Draw full toughness Bar")
            public boolean fullToughnessBar = false;

            @Config.Name("Display low armor warning")
            public boolean lowArmorWarning = false;

            @Config.Name("Display low health warning")
            public boolean lowHealthWarning = true;

            @Config.Name("Low health warning threshold")
            @Config.RangeDouble(min = 0, max = 1)
            public double lowHealthThreshold = .2;

            @Config.Name("Display low hunger warning")
            public boolean lowHungerWarning = true;

            @Config.Name("Bar left order")
            public String[] leftorder = new String[]{"health","armor","absorption","lavacharm","lavacharm2"};

            @Config.Name("Bar right order")
            public String[] rightorder = new String[]{"blood","healthmount","food","armortoughness","thirst","air","flighttiara","decay"};

            @Config.Name("Low hunger warning threshold")
            public double lowHungerThreshold = .3;

            public class HungerBarConfig {

                @Config.Name("Show Saturation Bar")
                public boolean showSaturationBar = true;

                @Config.Name("Show Hunger Bar While on Mount")
                public boolean showHungerBarOnMount = false;

                @Config.Name("Show Held Food Overlay")
                public boolean showHeldFoodOverlay = true;

                @Config.Name("Show Exhaustion Overlay")
                public boolean showExhaustionOverlay = true;

                @Config.Name("Transistion speed of bar")
                @Config.RangeDouble(min = 0.001, max = .2)
                public float transitionSpeed = .02f;
            }
        }

    }

    public static class ConfigNumbers {

        private static final String s = "Show Numbers For ";

        @Config.Name("Percentage based")
        public boolean showPercent = false;

        @Config.Comment("Numbers info")

        @Config.Name(s+"Health")
        public boolean showHealthNumbers = true;

        @Config.Name(s+"Lava")
        public boolean showLavaNumbers = true;

        @Config.Name(s+"Hunger")
        public boolean showHungerNumbers = true;

        @Config.Name(s+"Thirst")
        public boolean showThirstNumbers = true;

        @Config.Name(s+"Tiara")
        public boolean showTiaraNumbers = true;

        @Config.Name(s+"Armor Toughness")
        public boolean showArmorToughnessNumbers = true;

        @Config.Name(s+"Armor")
        public boolean showArmorNumbers = true;

        @Config.Name(s+"Mount Health")
        public boolean showMountHealthNumbers;

        @Config.Name(s+"Oxygen Bar")
        public boolean showOxygenNumbers;

        @Config.Name("Numbers scale")
        @Config.Comment("unused")
        @Config.RangeDouble(min = 0, max = 1)
        public double numberScale = .75;
    }

    public static class ConfigColors {
        @Config.Name("Advanced Options")
        public AdvancedColors advancedColors = new AdvancedColors();
        @Config.Name("Hunger Bar Color")
        public String hungerBarColor = "#B34D00";
        @Config.Name("Hunger Debuff Color")
        public String hungerBarDebuffColor = "#249016";
        @Config.Name("Oxygen Bar Color")
        public String oxygenBarColor = "#00E6E6";
        @Config.Name("Saturation Bar Color")
        public String saturationBarColor = "#FFCC00";
        @Config.Name("Saturation Debuff Color")
        public String saturationBarDebuffColor = "#87BC00";


        public class AdvancedColors {
            @Config.Comment("Colors must be specified in #RRGGBB format")
            @Config.Name("Armor color values")
            public String[] armorColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};
            @Config.Name("Armor Toughness Bar Color")
            public String[] armorToughnessColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};
            @Config.Name("Absorption Bar Color")
            public String[] absorptionColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Absorption Poison Bar Color")
            public String[] absorptionPoisonColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Absorption Wither Bar Color")
            public String[] absorptionWitherColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Health fractions")
            public double[] normalFractions = new double[]{.25, .5, .75};
            @Config.Name("Colors")
            public String[] normalColors = new String[]{"#FF0000", "#FFFF00", "#00FF00"};

            @Config.Name("Poisoned fractions")
            public double[] poisonedFractions = new double[]{.25, .5, .75};
            @Config.Name("Poisoned Colors")
            public String[] poisonedColors = new String[]{"#00FF00", "#55FF55", "#00FF00"};

            @Config.Name("Withered fractions")
            public double[] witheredFractions = new double[]{.25, .5, .75};
            @Config.Name("Withered Colors")
            public String[] witheredColors = new String[]{"#555555", "#AAAAAA", "#555555"};
        }
    }

    public static class ConfigWarnings {
        @Config.Name("Show Advanced Rocketry warning")
        @Config.Comment("Warning when advanced rocketry is installed")
        public boolean advancedRocketryWarning = true;

        @Config.Name("Show Rustic warning")
        @Config.Comment("Warning when Rustic is installed")
        public boolean rusticWarning = true;
    }

    @Mod.EventBusSubscriber(modid = ClassicBar.MODID)
    public static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ClassicBar.MODID)) {
                ConfigManager.sync(ClassicBar.MODID, Config.Type.INSTANCE);
                idiots.idiotsTryingToParseBadHexColorsDOTJpeg();
                idiots.emptyArrayFixer();
                EventHandler.setup();
                ClassicBar.logger.info("Syncing Classic Bar Configs");
            }
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
            idiots.idiotsTryingToParseBadHexColorsDOTJpeg();
            EntityPlayer p = e.player;
            if (Loader.isModLoaded("advancedrocketry") && warnings.advancedRocketryWarning && general.overlays.displayToughnessBar) {

                p.sendMessage(new TextComponentString(TextFormatting.RED + "Toughness bar may not display correctly, change the placement in advanced rocketry config." +
                        " This is NOT a bug."));
            }
            if (Loader.isModLoaded("rustic") && warnings.rusticWarning) {
                p.sendMessage(new TextComponentString(TextFormatting.RED + "Armor bar may not display correctly, disable Rustic's extra armor overlay amd restart the game." +
                        " This is NOT a bug."));
            }
        }
    }

    public static class ConfigMods {
        @Config.Name("Lava Bar Color")
        public String lavaBarColor = "#FF8000";

        @Config.Name("Thirst Bar Color")
        public String thirstBarColor = "#1C5EE4";

        @Config.Name("Flight Bar Color")
        public String flightBarColor = "#FFFFFF";

        @Config.Name("Hydration Bar Color")
        public String hydrationBarColor = "#00A3E2";

        @Config.Name("Dehydration Bar Color")
        @Config.Comment("This is the overlay for thirst when underneath the effect")
        public String deHydrationBarColor = "#5A891C";

        @Config.Name("Dehydration Secondary Bar Color")
        @Config.Comment("This is the overlay for hydration when underneath the effect")
        public String deHydrationSecondaryBarColor = "#85CF25";
    }
}