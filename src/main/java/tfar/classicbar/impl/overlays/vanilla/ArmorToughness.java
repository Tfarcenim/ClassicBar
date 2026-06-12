package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.joml.Vector2i;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.IconData;
import tfar.classicbar.impl.overlays.templates.StackingBarOverlay;

import java.util.List;

public class ArmorToughness extends StackingBarOverlay {

    public static final BarInfo INFO = BarInfo.getBuilder("armor_toughness")
            .setShouldRender(player -> player.getAttributeValue(Attributes.ARMOR_TOUGHNESS) >= 1)
            .setNumerator(player -> (float) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS))
            .setDenominator(fixed(20f))
            .setIconData(new IconData(List.of(new Vector2i(83,0))))
            .build();

    public ArmorToughness(BarSettings barSettings) {
        super(INFO,barSettings,CODEC);
    }

    public static final Codec<ArmorToughness> CODEC = RecordCodecBuilder.create(
            o -> codecStart(o).apply(o,ArmorToughness::new)
    );

}
