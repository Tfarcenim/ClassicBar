package tfar.classicbar.api;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

// Changed: new class (did not exist in origin/1.20.1-original). Replaces the pattern where
// each overlay class overrode shouldRenderText() and getIconRL() individually.
// Instances are written to and read from per-bar JSON files in config/classicbar/,
// injected into each overlay via BarOverlay.setBarSettings() at config load time.
public class BarSettings {
    public boolean show_text; // replaces each overlay's shouldRenderText() override
    public ResourceLocation icon; // replaces each overlay's getIconRL() override

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("show_text",show_text);
        jsonObject.addProperty("icon",icon.toString());
        return jsonObject;
    }

    public BarSettings copy() {
        BarSettings copy = new BarSettings();
        copy.show_text = show_text;
        copy.icon = icon;
        return copy;
    }

}
