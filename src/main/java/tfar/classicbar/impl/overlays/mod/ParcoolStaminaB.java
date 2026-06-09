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
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.OneColorBar;
import tfar.classicbar.util.Color;

public class ParcoolStaminaB extends OneColorBar {

    public static final String name = "parcool:stamina";
    public static final ResourceLocation ICONS = new ResourceLocation("parcool:textures/gui/stamina_bar.png");

    public static final BarInfo INFO = new BarInfo(name,
            player -> !checkConfigs() && getRatio(player) < 1
            ,player -> IStamina.get(player).get(),player -> IStamina.get(player).getMaxStamina());

    public ParcoolStaminaB(BarSettings settings, Color color) {
        super(INFO,settings,color);
    }

    protected static float getRatio(Player player) {
        return (float)IStamina.get(player).get() / IStamina.get(player).getMaxStamina();
    }

    public static final Codec<ParcoolStaminaB> CODEC = RecordCodecBuilder.create(o -> altCodecStart(o)
            .apply(o, ParcoolStaminaB::new)
    );

    @Override
    public Codec<? extends BarOverlayImpl> getCodec() {
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