package tfar.classicbar.compat;

import net.neoforged.fml.ModList;

public enum ModCompat {
    vampirism("vampirism"), parcool("parcool"), toughasnails("toughasnails");
    public final boolean loaded;
    final String modid;
    ModCompat(String modid) {
        this.modid = modid;
        loaded = ModList.get().isLoaded(modid);
    }
    
}
