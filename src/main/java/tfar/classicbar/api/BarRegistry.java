package tfar.classicbar.api;

import tfar.classicbar.api.colorprovider.*;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.SimpleBarOverlay;
import tfar.classicbar.impl.overlays.mod.VampirismBlood;
import tfar.classicbar.impl.overlays.mod.ParcoolStaminaB;
import tfar.classicbar.impl.overlays.mod.ToughAsNailsThirst;
import tfar.classicbar.impl.overlays.vanilla.*;

import java.util.LinkedHashMap;

public class BarRegistry {
    public static final LinkedHashMap<String, BarOverlay> REGISTRY = new LinkedHashMap<>();

    public static <B extends BarOverlay> void registerBar(B defaults) {
        REGISTRY.put(defaults.name(),defaults);
    }

    public static void init() {

    }

    static {
        registerBar(new Health(BarSettings.getBuilder().setColorProvider(TransitioningEffectColorProvider.DEFAULT).build()));
        registerBar(new MountHealth(BarSettings.getBuilder().setSide(BarSide.RIGHT).setColorProvider(TransitioningColorProvider.DEFAULT).build()));
        registerBar(new Food(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(DualEffectColorProvider.FOOD)
                .build(),true,true,true));
        registerBar(new ToughAsNailsThirst(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(DualEffectColorProvider.THIRST)
                .setIcon(ToughAsNailsThirst.OVERLAY).build(),true,true,true));
        registerBar(new Air(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.hex2Color("#00E6E6")))
                .build()));
        registerBar(new Armor(BarSettings.getBuilder().fitted()
                .setColorProvider(StackingColorProvider.DEFAULT_ARMOR)
                .build()));
        registerBar(new Absorption(BarSettings.getBuilder().fitted()
                .setColorProvider(StackingEffectColorProvider.DEFAULT_ABSORPTION).build()));
        registerBar(new ArmorToughness(BarSettings.getBuilder().setSide(BarSide.RIGHT).fitted()
                .setColorProvider(StackingColorProvider.DEFAULT_ARMOR)
                .setIcon(BarOverlayImpl.BAR).build()));
        registerBar(new VampirismBlood(BarSettings.getBuilder()
                .setColorProvider(new SingleColorProvider(Color.RED))
                .setIcon(VampirismBlood.VAMPIRISM_ICONS).build()));
        registerBar(SimpleBarOverlay.createFeathers(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.FEATHERS))
                .setIcon(SimpleBarOverlay.FEATHERS_ICONS).build()));
        registerBar(new ParcoolStaminaB(BarSettings.getBuilder().setSide(BarSide.RIGHT)
                .setColorProvider(new SingleColorProvider(Color.YELLOW)).setIcon(ParcoolStaminaB.ICONS).build()));
    }
}
