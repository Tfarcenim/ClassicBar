package tfar.classicbar.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

public enum ModCompat {
    vampirism, feathers,legendarysurvivaloverhaul,parcool,thirst,toughasnails;
    public final boolean loaded;

    public ResourceLocation id(String path){
        return new ResourceLocation(name(), path);
    }

    ModCompat() {
        loaded = ModList.get().isLoaded(name());
    }
    
}
