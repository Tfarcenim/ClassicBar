package tfar.classicbar.impl.overlays;

import com.elenai.feathers.api.FeathersHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.IconData;

import java.util.List;

public class SimpleBarOverlay extends BarOverlayImpl {


    private Codec<? extends SimpleBarOverlay> codec;

    public SimpleBarOverlay(BarSettings settings,BarInfo barInfo,Codec<? extends SimpleBarOverlay> codec) {
        super(barInfo,settings);
        this.codec = codec;
    }

    public static final ResourceLocation FEATHERS_ICONS = new ResourceLocation("feathers", "textures/gui/icons.png");

    public static SimpleBarOverlay createFeathers(BarSettings barSettings) {
        BarInfo info = BarInfo.getBuilder("feathers_feathers")
                .requireDependency(ModCompat.feathers.name())
                .setNumerator(player -> FeathersHelper.getFeathers())
                .setDenominator(player -> FeathersHelper.getMaxFeathers())
                .setIconData(new IconData(List.of(new Vector2i(34,0)))).build();

        Codec<? extends SimpleBarOverlay> codec = RecordCodecBuilder.create(inst ->
                codecStart(inst).apply(inst, SimpleBarOverlay::createFeathers));
        return new SimpleBarOverlay(barSettings, info, codec);
    }

    @Override
    public Codec<? extends BarOverlay> codec() {
        return codec;
    }
}
