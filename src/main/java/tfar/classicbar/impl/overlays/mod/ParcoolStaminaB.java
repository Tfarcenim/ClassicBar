package tfar.classicbar.impl.overlays.mod;


import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;

public class ParcoolStaminaB extends BarOverlayImpl {

    public static final ResourceLocation ICONS = new ResourceLocation("parcool:textures/gui/stamina_bar.png");

    public static final BarInfo INFO = BarInfo.getBuilder("parcool_stamina")
            .requireDependency(ModCompat.parcool.name())
            .setShouldRender(player -> !checkConfigs() && getRatio(player) < 1)
            .addNumerator(player -> IStamina.get(player).get())
            .setDenominator(player -> IStamina.get(player).getMaxStamina()).build();

    public ParcoolStaminaB(BarSettings settings) {
        super(INFO,settings);
    }

    protected static float getRatio(Player player) {
        return (float)IStamina.get(player).get() / IStamina.get(player).getMaxStamina();
    }

    public static final Codec<ParcoolStaminaB> CODEC = RecordCodecBuilder.create(o -> codecStart(o)
            .apply(o, ParcoolStaminaB::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> codec() {
        return CODEC;
    }

    public static boolean checkConfigs() {
        return ParCoolConfig.Client.StaminaHUDType.get() == HUDType.Light;
    }

    @Override
    public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
        int xStart = width / 2 + getIconOffset();
        int yStart = height - vOffset;
        //Draw stamina icon
        IStamina stamina = IStamina.get(player);
        int textureX = stamina.isExhausted() ? 16 : 0;
        graphics.blit(getIconRL(),xStart, yStart, textureX, 119, 8, 9, 128, 128);
    }
}