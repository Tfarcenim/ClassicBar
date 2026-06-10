package tfar.classicbar.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import tfar.classicbar.api.colorprovider.ColorProvider;
import tfar.classicbar.api.colorprovider.SingleColorProvider;
import tfar.classicbar.impl.BarOverlayImpl;

//these are common settings that the player can adjust
public record BarSettings(boolean enabled, BarSide side, boolean fitted, ColorProvider colorProvider, boolean show_text,int text_index,
                          boolean show_icon, ResourceLocation icon) {

    public static final MapCodec<BarSettings> CODEC = RecordCodecBuilder.mapCodec(
            objectInstance -> objectInstance.group(
                    Codec.BOOL.fieldOf("enabled").forGetter(BarSettings::enabled),
                    BarSide.CODEC.fieldOf("side").forGetter(BarSettings::side),
                    Codec.BOOL.fieldOf("fitted").forGetter(BarSettings::fitted),
                    ColorProvider.CODEC.fieldOf("color_provider").forGetter(BarSettings::colorProvider),
                    Codec.BOOL.fieldOf("show_text").forGetter(BarSettings::show_text),
                    Codec.INT.fieldOf("text_index").forGetter(BarSettings::text_index),
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
        private ColorProvider colorProvider = new SingleColorProvider(Color.WHITE);
        private boolean fitted = false;
        private boolean show_text = true;
        private int text_index = 0;
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

        public Builder fitted() {
            this.fitted = true;
            return this;
        }

        public Builder setShowText(boolean show_text) {
            this.show_text = show_text;
            return this;
        }

        public void setTextIndex(int text_index) {
            this.text_index = text_index;
        }

        public Builder setColorProvider(ColorProvider colorProvider) {
            this.colorProvider = colorProvider;
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
            return new BarSettings(enabled,side,fitted,colorProvider,show_text,text_index,show_icon,icon);
        }
    }
}
