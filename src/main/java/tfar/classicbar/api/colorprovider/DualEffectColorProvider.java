package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.Color;
import tfar.classicbar.compat.ModCompat;

public record DualEffectColorProvider(MobEffect effect,
                                      Color primary, Color secondary,
                                      Color primaryUnderEffect,Color secondaryUnderEffect) implements ColorProvider {

    public static final MapCodec<DualEffectColorProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("mob_effect").forGetter(DualEffectColorProvider::effect),
            Color.HEX_CODEC.fieldOf("primary").forGetter(DualEffectColorProvider::primary),
            Color.HEX_CODEC.fieldOf("secondary").forGetter(DualEffectColorProvider::secondary),
            Color.HEX_CODEC.fieldOf("primary_under_effect").forGetter(DualEffectColorProvider::primaryUnderEffect),
            Color.HEX_CODEC.fieldOf("secondary_under_effect").forGetter(DualEffectColorProvider::secondaryUnderEffect)
    ).apply(instance, DualEffectColorProvider::new));


    public static final DualEffectColorProvider FOOD = new DualEffectColorProvider(MobEffects.HUNGER,
            Color.hex2Color("#B34D00"),Color.hex2Color("#FFCC00"),Color.hex2Color("#249016"),Color.hex2Color("#87BC00"));

    //use a method as otherwise it would be null
    public static DualEffectColorProvider thirst() {
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(ModCompat.toughasnails.id("thirst"));
        return new DualEffectColorProvider(effect,//don't use TANEffects.THIRST here, it crashes
                Color.hex2Color("#1C5EE4"),Color.hex2Color("#00A3E2"),Color.hex2Color("#5A891C"),Color.hex2Color("#85CF25"));
    }

    @Override
    public Color getColor(Player player, float ratio, int layer) {
        if (player.hasEffect(effect)) {
            if (layer == 0) return primaryUnderEffect;
            return secondaryUnderEffect;
        } else {
            if (layer == 0) return primary;
            return secondary;
        }
    }

    @Override
    public ColorProviderSerializer<?> getSerializer() {
        return ColorProviderSerializers.DUAL_EFFECT;
    }
}