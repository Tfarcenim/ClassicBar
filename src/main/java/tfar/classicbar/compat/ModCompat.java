package tfar.classicbar.compat;

import net.neoforged.fml.ModList;

public enum ModCompat {
    vampirism("vampirism"), parcool("parcool"), toughasnails("toughasnails"),
    thirstWasTaken("thirst"), // Changed: new entry; adds compat for the "Thirst Was Taken" mod (modid "thirst")
    homeostatic("homeostatic"); // Changed: new entry; adds compat for the Homeostatic mod
    public final boolean loaded;
    final String modid;
    ModCompat(String modid) {
        this.modid = modid;
        loaded = ModList.get().isLoaded(modid);
    }
    
}
