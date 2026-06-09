package tfar.classicbar.impl.overlays.mod;

import com.elenai.feathers.api.FeathersHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.overlays.OneColorBar;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class FeathersB extends OneColorBar {

	public static final BarInfo INFO = new BarInfo("feathers",ModCompat.feathers.name(),
			player -> true,player -> FeathersHelper.getFeathers(),
			player -> FeathersHelper.getMaxFeathers());

	public FeathersB(BarSettings settings,Color color) {
		super(INFO,settings,color);
	}
	public static final ResourceLocation ICONS = new ResourceLocation("feathers", "textures/gui/icons.png");


	public static final Codec<FeathersB> CODEC = RecordCodecBuilder.create(inst ->
			altCodecStart(inst).apply(inst, FeathersB::new));

	@Override
	public Codec<? extends BarOverlayImpl> getCodec() {
		return CODEC;
	}


	@Override
	public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		int xStart = width / 2 + 10;
		int yStart = height - vOffset;
		//Draw feathers icon
		ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart + 82, yStart, 34, 0, 9, 9);
	}
}
