package tfar.classicbar.compat;

import net.minecraftforge.fml.ModList;

public enum ModCompat {
    vampirism, feathers,parcool,thirst,toughasnails;
    public final boolean loaded;
    ModCompat() {
        loaded = ModList.get().isLoaded(name());
    }
    
}
