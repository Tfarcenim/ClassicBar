package tfar.classicbar;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ClassicBar.MODID)
public class ModConfig {

    @Config.Name("Armor bar colors")
    @Config.Comment("Colors must be specified in #RRGGBB format")
    public static String[] armorColorValues = new String[]{"#AAAAAA", "#FF5500", "#FFC747", "#27FFE3", "#00FF00", "#7F00FF"};

    @Config.Name("Show Icons")
    @Config.Comment("Whether to show icons next to the bars")
    public static boolean displayIcons = true;

    @Config.Name("Display Armor Toughness Bar")
    public static boolean displayToughnessBar = true;

    @Config.Name("Draw full absorption Bar")
    public static boolean fullAbsorptionBar = false;

    @Config.Comment("Numbers info")

    @Config.Name("Show Numbers")
    public static boolean showNumbers = true;
    @Config.Name("Percentage based")
    public static boolean showPercent = false;

    @Config.Name("Show warning")
    @Config.Comment("Warning when advanced rocketry is installed")
    public static boolean advancedRocketryWarning = true;




    @Mod.EventBusSubscriber(modid = ClassicBar.MODID)
    public static class ConfigEventHandler {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ClassicBar.MODID))

                ConfigManager.sync(ClassicBar.MODID, Config.Type.INSTANCE);
            ClassicBar.proxy.refreshResources();

            System.out.println("Syncing Classic Bar Configs");
        }
    }
}
