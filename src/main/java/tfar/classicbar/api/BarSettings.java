package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.impl.BarOverlayImpl;

//these are common settings that the player can adjust
public record BarSettings(boolean enabled,BarSide side,boolean fitted, boolean show_text,boolean show_icon, ResourceLocation icon) {

    public static final MapCodec<BarSettings> CODEC = RecordCodecBuilder.mapCodec(
            objectInstance -> objectInstance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(BarSettings::enabled),
                    BarSide.CODEC.fieldOf("side").forGetter(BarSettings::side),
                    Codec.BOOL.fieldOf("fitted").forGetter(BarSettings::fitted),
                    Codec.BOOL.fieldOf("show_text").forGetter(BarSettings::show_text),
                    Codec.BOOL.fieldOf("show_icon").forGetter(BarSettings::show_icon),
                    ResourceLocation.CODEC.fieldOf("icon")
                            .forGetter(BarSettings::icon)).apply(objectInstance,BarSettings::new)
    );

    public static BarSettings.Builder getBuilder() {
        return new BarSettings.Builder();
    }

    public static class Builder {
        private boolean enabled = true;
        private BarSide side = BarSide.LEFT;
        private boolean fitted = false;
        private boolean show_text = true;
        private boolean show_icon = true;
        private ResourceLocation icon = BarOverlayImpl.GUI_ICONS_LOCATION;

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setSide(BarSide side) {
            this.side = side;
            return this;
        }

        public Builder setFitted(boolean fitted) {
            this.fitted = fitted;
            return this;
        }

        public Builder setShowText(boolean show_text) {
            this.show_text = show_text;
            return this;
        }

        public Builder setIcon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public Builder setShowIcon(boolean show_icon) {
            this.show_icon = show_icon;
            return this;
        }

        public BarSettings build() {
            return new BarSettings(enabled,side,fitted,show_text,show_icon,icon);
        }
    }
}
