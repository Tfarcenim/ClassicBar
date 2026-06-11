package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2i;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.IconData;
import tfar.classicbar.impl.overlays.StackingBarOverlay;

import java.util.List;

public class Armor extends StackingBarOverlay {

    public static final BarInfo INFO = BarInfo.getBuilder("armor")
            .setShouldRender(player -> player.getArmorValue() >= 1)
            .setNumerator(Player::getArmorValue)
            .setDenominator(fixed(20f))
            .setIconData(new IconData(List.of(new Vector2i(43,9)))).build();

    public Armor(BarSettings barSettings) {
        super(INFO,barSettings,CODEC);
    }

    public static final Codec<Armor> CODEC = RecordCodecBuilder.create(
            objectInstance -> codecStart(objectInstance)
                    .apply(objectInstance,Armor::new)
    );
}