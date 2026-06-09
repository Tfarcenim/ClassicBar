package tfar.classicbar.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.impl.overlays.mod.Thirst;
import tfar.classicbar.impl.overlays.vanilla.*;
import tfar.classicbar.util.ModUtils;

import java.util.HashMap;
import java.util.Map;

public class BarRegistry {
    public static final Map<String, Codec<? extends BarOverlay>> REGISTRY = new HashMap<>();

    public static final Map<String, JsonElement> DEFAULTS = new HashMap<>();

    public static void registerBar(String name,Codec<? extends BarOverlay> codec,JsonElement defaults) {
        REGISTRY.put(name,codec);
        DEFAULTS.put(name,defaults);
    }

    static {
        registerBar("air", Air.CODEC,createAirJson());
        registerBar("absorption", Absorption.CODEC,createAbsorptionJson());
        registerBar("armor", Armor.CODEC,createArmorJson());
        registerBar("blood", Blood.CODEC,createBloodJson());
        registerBar("food", Hunger.CODEC,createHealthJson());
        registerBar("health", Health.CODEC,createFoodJson());
        registerBar("thirst", Thirst.CODEC,createThirstJson());
    }

    static JsonElement createAirJson() {
        return Air.CODEC.encodeStart(JsonOps.INSTANCE, new Air(
                BarSettings.getBuilder().build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createAbsorptionJson() {
        return Air.CODEC.encodeStart(JsonOps.INSTANCE, new Air(
                BarSettings.getBuilder().build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createArmorJson() {
        return Air.CODEC.encodeStart(JsonOps.INSTANCE, new Air(
                BarSettings.getBuilder().build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createBloodJson() {
        return Blood.CODEC.encodeStart(JsonOps.INSTANCE, new Blood(
                BarSettings.getBuilder()
                        .setIcon(ModUtils.VAMPIRISM_ICONS)
                        .build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createFoodJson() {
        return Hunger.CODEC.encodeStart(JsonOps.INSTANCE, new Hunger(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createHealthJson() {
        return Health.CODEC.encodeStart(JsonOps.INSTANCE, new Health(
                BarSettings.getBuilder().build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createThirstJson() {
        return Thirst.CODEC.encodeStart(JsonOps.INSTANCE, new Thirst(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static void makeDefaultBarSettings() {

        /*BarSettings absorbSettings = defaultBarSettings.copy();
        defaults.put("absorption",absorbSettings);

        BarSettings airSettings = defaultBarSettings.copy();
        defaults.put("air",airSettings);

        BarSettings armorSettings = defaultBarSettings.copy();
        defaults.put("armor",armorSettings);

        BarSettings armorToughnessSettings = defaultBarSettings.copy();
        armorToughnessSettings.icon = BarOverlayImpl.ICON_BAR;
        defaults.put("armor_toughness",armorToughnessSettings);

        BarSettings bloodSettings = defaultBarSettings.copy();
        bloodSettings.icon = ModUtils.VAMPIRISM_ICONS;
        defaults.put("blood",bloodSettings);

        BarSettings healthMountSettings = defaultBarSettings.copy();
        defaults.put("health_mount",healthMountSettings);*/
    }
}
