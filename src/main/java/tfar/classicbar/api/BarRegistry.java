package tfar.classicbar.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.impl.overlays.mod.FeathersB;
import tfar.classicbar.impl.overlays.mod.ParcoolStaminaB;
import tfar.classicbar.impl.overlays.mod.Thirst;
import tfar.classicbar.impl.overlays.vanilla.*;
import tfar.classicbar.util.Color;

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
        registerBar("armor_toughness", ArmorToughness.CODEC,createArmorToughnessJson());
        registerBar("blood", Blood.CODEC,createBloodJson());
        registerBar("feathers", FeathersB.CODEC,createFeathersJson());
        registerBar("food", Food.CODEC,createFoodJson());
        registerBar("health", Health.CODEC,createHealthJson());
        registerBar("mount_health", MountHealth.CODEC,createMountHealthJson());
        registerBar("thirst", Thirst.CODEC,createThirstJson());
    }

    static JsonElement createAirJson() {
        return Air.CODEC.encodeStart(JsonOps.INSTANCE, new Air(
                BarSettings.getBuilder().build(),Color.hex2Color("#00E6E6")
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createAbsorptionJson() {
        return Absorption.CODEC.encodeStart(JsonOps.INSTANCE, new Absorption(
                BarSettings.getBuilder().setFitted(true).build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createArmorJson() {
        return Armor.CODEC.encodeStart(JsonOps.INSTANCE, new Armor(
                BarSettings.getBuilder().setFitted(true).build()
        )).getOrThrow(false, JsonParseException::new);
    }



    static JsonElement createArmorToughnessJson() {
        return ArmorToughness.CODEC.encodeStart(JsonOps.INSTANCE, new ArmorToughness(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).setFitted(true).setIcon(BarOverlayImpl.BAR).build()
        )).getOrThrow(false, JsonParseException::new);
    }


    static JsonElement createBloodJson() {
        return Blood.CODEC.encodeStart(JsonOps.INSTANCE, new Blood(
                BarSettings.getBuilder()
                        .setIcon(Blood.VAMPIRISM_ICONS)
                        .build()
        , Color.RED)).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createFeathersJson() {
        return FeathersB.CODEC.encodeStart(JsonOps.INSTANCE, new FeathersB(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).setIcon(FeathersB.ICONS).build()
        ,Color.FEATHERS)).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createFoodJson() {
        return Food.CODEC.encodeStart(JsonOps.INSTANCE, new Food(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).build()
        ,true,true,true)).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createHealthJson() {
        return Health.CODEC.encodeStart(JsonOps.INSTANCE, new Health(
                BarSettings.getBuilder().build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createMountHealthJson() {
        return MountHealth.CODEC.encodeStart(JsonOps.INSTANCE, new MountHealth(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).build()
        )).getOrThrow(false, JsonParseException::new);
    }

    static JsonElement createStaminaJson() {
        return ParcoolStaminaB.CODEC.encodeStart(JsonOps.INSTANCE, new ParcoolStaminaB(
                BarSettings.getBuilder().setSide(BarSide.RIGHT).build()
        ,Color.YELLOW)).getOrThrow(false, JsonParseException::new);
    }


    static JsonElement createThirstJson() {
        return Thirst.CODEC.encodeStart(JsonOps.INSTANCE, new Thirst(
                BarSettings.getBuilder().setSide(BarSide.RIGHT)
                        .setIcon(Thirst.OVERLAY)
                        .build(),true,true
        )).getOrThrow(false, JsonParseException::new);
    }
}
