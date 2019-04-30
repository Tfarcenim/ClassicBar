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

import static lumien.randomthings.asm.ClassTransformer.logger;
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
        @Config.Name("Texture Style")
        @Config.Comment("Texture Style of bars: 0 for default, 1 for new")
        public int style = 1;
        public class BarOverlays {

            @Config.Name("Hunger Bar Overlays")
            public HungerBarConfig hunger = new HungerBarConfig();

            @Config.Name("Display Armor Toughness Bar")
            @Config.RequiresMcRestart
            @Config.Comment("REQUIRES A RESTART TO APPLY!")
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

            @Config.Name("Swap absorption & armor?")
            public boolean swap = false;

            public class HungerBarConfig {

                @Config.Name("Show Saturation Bar")
                public boolean showSaturationBar = true;

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


        public class AdvancedColors {
            @Config.Comment("Colors must be specified in #RRGGBB format")
            @Config.Name("Armor color values")
            public String[] armorColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};
            @Config.Name("Absorption Bar Color")
            public String[] absorptionColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Absorption Poison Bar Color")
            public String[] absorptionPoisonColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Absorption Wither Bar Color")
            public String[] absorptionWitherColorValues = new String[]{"#D4AF37","#C2C73B","#8DC337","#36BA77","#4A5BC4","#D89AE2","#DF9DC7","#DFA99D","#D4DF9D","#3E84C6","#B8C1E8","#DFDFDF"};

            @Config.Name("Health fractions")
            public Float[] healthFractions = new Float[]{.25f, .5f, .75f};
            @Config.Name("Colors")
            public String[] hexColors = new String[]{"#FF0000", "#FFFF00", "#00FF00"};

            @Config.Name("Poisoned fractions")
            public Float[] poisonedFractions = new Float[]{.25f, .5f, .75f};
            @Config.Name("Poisoned Colors")
            public String[] poisonedColors = new String[]{"#00FF00", "#55FF55", "#00FF00"};

            @Config.Name("Withered fractions")
            public Float[] witheredFractions = new Float[]{.25f, .5f, .75f};
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
                logger.info("Syncing Classic Bar Configs");
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

    }
}