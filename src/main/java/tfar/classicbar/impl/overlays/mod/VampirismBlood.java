package tfar.classicbar.impl.overlays.mod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarType;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.ModUtils;

public class VampirismBlood extends BarOverlayImpl {

    public static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");

    public static final BarInfo INFO = BarInfo.getBuilder("vampirism_blood")
            .requireDependency(ModCompat.vampirism.name())
            .setShouldRender(player -> VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION)
            .addNumerator(VampirismBlood::getNumerator)
            .setDenominator(VampirismBlood::getDenominator).build();

    public VampirismBlood(BarSettings settings) {
        super(INFO,settings);
    }

    public static final Codec<VampirismBlood> CODEC = RecordCodecBuilder.create(
            objectInstance -> codecStart(objectInstance).apply(objectInstance, VampirismBlood::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return CODEC;
    }

    public static float getNumerator(Player player) {
        IBloodStats stats = VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).orElse(null);
        return stats != null ? stats.getBloodLevel() : 0;
    }

    public static float getDenominator(Player player) {
        IBloodStats stats = VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).orElse(null);
        //don't divide by zero
        return stats != null ? stats.getMaxBlood() : 1;
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw blood icon
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 0, 0, 9, 9);
        ModUtils.drawTexturedModalRect(getIconRL(),graphics, xStart, yStart, 9, 0, 9, 9);
    }
}
