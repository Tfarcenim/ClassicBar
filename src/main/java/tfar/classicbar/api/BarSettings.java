package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.impl.BarOverlayImpl;

import java.util.Set;

public record BarSettings(boolean enabled,BarSide side, boolean show_text, ResourceLocation icon) {

    public static final MapCodec<BarSettings> CODEC = RecordCodecBuilder.mapCodec(
            objectInstance -> objectInstance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(BarSettings::enabled),
                    BarSide.CODEC.fieldOf("side").forGetter(BarSettings::side),
                    Codec.BOOL.fieldOf("show_text").forGetter(BarSettings::show_text),
                    ResourceLocation.CODEC.fieldOf("icon")
                            .forGetter(BarSettings::icon)).apply(objectInstance,BarSettings::new)
    );

    public static BarSettings.Builder getBuilder() {
        return new BarSettings.Builder();
    }

    public static class Builder {
        private boolean enabled = true;
        private BarSide side = BarSide.LEFT;
        private boolean show_text = true;
        private ResourceLocation icon = BarOverlayImpl.GUI_ICONS_LOCATION;

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setSide(BarSide side) {
            this.side = side;
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

        public BarSettings build() {return new BarSettings(enabled,side,show_text,icon);}
    }
}
