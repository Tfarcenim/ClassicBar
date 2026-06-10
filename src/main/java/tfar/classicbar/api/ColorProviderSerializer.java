package tfar.classicbar.api;

import com.mojang.serialization.MapCodec;

public record ColorProviderSerializer<C extends ColorProvider>(String name, MapCodec<C> codec) {}
