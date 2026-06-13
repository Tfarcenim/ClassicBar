package tfar.classicbar.api;

import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import tfar.classicbar.api.colorprovider.*;
import tfar.classicbar.impl.overlays.templates.BarOverlayImpl;
import tfar.classicbar.impl.overlays.templates.SimpleBarOverlay;
import tfar.classicbar.impl.overlays.mod.VampirismBlood;
import tfar.classicbar.impl.overlays.mod.ParcoolStaminaB;
import tfar.classicbar.impl.overlays.mod.ToughAsNailsThirst;
import tfar.classicbar.impl.overlays.vanilla.*;

import java.util.LinkedHashMap;

public class BarRegistry {
    public static final LinkedHashMap<String, BarOverlay> REGISTRY = new LinkedHashMap<>();

    public static <B extends BarOverlay> void registerBar(B defaults) {
        if (defaults.dependenciesMet()) {//do not register bars that have missing dependencies
            REGISTRY.put(defaults.name(), defaults);
        }
    }

    public static void init() {

    }

    static {
        registerBar(new Health(BarSettings.getBuilder()
                .setDisablesOverlay(VanillaGuiOverlay.PLAYER_HEALTH.id())
                .setColorProvider(TransitioningEffectColorProvider.DEFAULT).build()));
        registerBar(new MountHealth(BarSettings.getBuilder()
                .setDisablesOverlay(VanillaGuiOverlay.MOUNT_HEALTH.id())
                .setSide(BarSide.RIGHT).setColorProvider(TransitioningColorProvider.DEFAULT).build()));
        registerBar(new Food(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(DualEffectColorProvider.FOOD)
                .setDisablesOverlay(VanillaGuiOverlay.FOOD_LEVEL.id())
                .build(),true,true,true));
        registerBar(new ToughAsNailsThirst(BarSettings.getBuilder()
                .setDisablesOverlay(ToughAsNailsThirst.OVERLAY_ID)
                .setSide(BarSide.RIGHT)
                .setColorProvider(DualEffectColorProvider.thirst())
                .setIcon(ToughAsNailsThirst.OVERLAY).build(),true,true,true));
        registerBar(new Air(BarSettings.getBuilder()
                .setDisablesOverlay(VanillaGuiOverlay.AIR_LEVEL.id())
                .setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.hex2Color("#00E6E6")))
                .build()));
        registerBar(new Armor(BarSettings.getBuilder().setDisablesOverlay(VanillaGuiOverlay.ARMOR_LEVEL.id()).fitted()
                .setColorProvider(StackingColorProvider.DEFAULT_ARMOR)
                .build()));
        registerBar(new Absorption(BarSettings.getBuilder()//health already disables this
                .fitted()
                .setColorProvider(StackingEffectColorProvider.DEFAULT_ABSORPTION).build()));
        registerBar(new ArmorToughness(BarSettings.getBuilder().setSide(BarSide.RIGHT).fitted()
                .setColorProvider(StackingColorProvider.DEFAULT_ARMOR)
                .setIcon(BarOverlayImpl.BAR).build()));
        registerBar(new VampirismBlood(BarSettings.getBuilder()//.setDisablesOverlay(VampirismBlood.)nothing?
                .setColorProvider(new SingleColorProvider(Color.RED))
                .setSide(BarSide.RIGHT).setIcon(VampirismBlood.VAMPIRISM_ICONS).build()));
        registerBar(SimpleBarOverlay.createFeathers(BarSettings.getBuilder()
                        .setDisablesOverlay(SimpleBarOverlay.FEATHERS_OVERLAY_ID)
                .setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.FEATHERS))
                .setIcon(SimpleBarOverlay.FEATHERS_ICONS).build()));
        registerBar(new ParcoolStaminaB(BarSettings.getBuilder().setDisablesOverlay(ParcoolStaminaB.OVERLAY_ID).setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.YELLOW)).setIcon(ParcoolStaminaB.ICONS).build()));
    }
}
