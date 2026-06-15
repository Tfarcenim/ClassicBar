package tfar.classicbar.api.colorprovider;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.compat.ModCompat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public record DualEffectMapColorProvider(Color primary, Color secondary,
                                         Map<MobEffect, TwoColors> effectMap) implements ColorProvider {

    public static final MapCodec<DualEffectMapColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Color.HEX_CODEC.fieldOf("primary").forGetter(DualEffectMapColorProvider::primary),
            Color.HEX_CODEC.fieldOf("secondary").forGetter(DualEffectMapColorProvider::secondary),
            Codec.unboundedMap(BuiltInRegistries.MOB_EFFECT.byNameCodec(),TwoColors.CODEC)
                    .fieldOf("mob_effects").forGetter(DualEffectMapColorProvider::effectMap)
    ).apply(instance, DualEffectMapColorProvider::new));

    //use a method as otherwise it would be null
    public static DualEffectMapColorProvider survivalOverHaulThirst() {
        MobEffect heat_thirst = BuiltInRegistries.MOB_EFFECT.get(ModCompat.legendarysurvivaloverhaul.id("heat_thirst"));
        MobEffect thirst = BuiltInRegistries.MOB_EFFECT.get(ModCompat.legendarysurvivaloverhaul.id("thirst"));

        Map<MobEffect, TwoColors> effectMap = new HashMap<>();

        effectMap.put(heat_thirst, new TwoColors(Color.hex2Color("#e41c2c"),Color.hex2Color("#e27700")));
        effectMap.put(thirst, new TwoColors(Color.hex2Color("#5A891C"),Color.hex2Color("#85CF25")));

        return new DualEffectMapColorProvider(
                Color.hex2Color("#1C5EE4"), Color.hex2Color("#00A3E2"), effectMap);
    }

    @Override
    public Color getColor(Player player, float ratio, int layer) {

        for (Map.Entry<MobEffect, TwoColors> entry : effectMap.entrySet()) {
            if (player.hasEffect(entry.getKey())) {
                return layer == 0 ? entry.getValue().primary() : entry.getValue().secondary();
            }
        }

        if (layer == 0) return primary;
        return secondary;

    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.DUAL_EFFECT_MAP;
    }
}