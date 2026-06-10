package tfar.classicbar.api.colorprovider;

import com.mojang.serialization.MapCodec;

public record ColorProviderSerializer<C extends ColorProvider>(String name, MapCodec<C> codec) {}
