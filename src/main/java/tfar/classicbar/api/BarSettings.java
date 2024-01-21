package tfar.classicbar.api;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class BarSettings {
    public boolean show_text;
    public ResourceLocation icon;

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
