package tfar.classicbar.impl.overlays.mod;

import com.elenai.feathers.api.FeathersHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class FeathersB extends BarOverlayImpl {

	public FeathersB(BarSettings settings) {
		super("feathers", settings,ModCompat.feathers.name(), player -> (float)FeathersHelper.getFeathers()/FeathersHelper.getMaxFeathers());
	}
	public static final ResourceLocation ICONS = new ResourceLocation("feathers", "textures/gui/icons.png");


	public static final Codec<FeathersB> CODEC = RecordCodecBuilder.create(
			objectInstance -> objectInstance.group(BarSettings.CODEC.fieldOf("bar_settings")
					.forGetter(FeathersB::getBarSettings)
			).apply(objectInstance, FeathersB::new)
	);

	@Override
	public Codec<? extends BarOverlayImpl> getCodec() {
		return CODEC;
	}

	protected static final Color AQUA = Color.hex2Color("#22a5f0");

	@Override
	public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
		int barWidth = getBarWidth(player);
		int xStart = screenWidth / 2 + getHOffset();
		if (getSide() == BarSide.RIGHT) {
			xStart += WIDTH - barWidth;
		}
		int yStart = screenHeight - vOffset;

		//Bar background
		renderBarBackground(graphics,player,screenWidth,screenHeight,vOffset);
		//draw portion of bar based on feathers amount
		double f = xStart + WIDTH + 2 - barWidth;
		//AQUA.color2Gl();
		renderPartialBar(AQUA,graphics,f,yStart+2,barWidth);
	}

	@Override
	public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		//draw feathers amount
		double feathers = FeathersHelper.getFeathers();
		int xStart = width / 2 + getIconOffset();
		int yStart = height - vOffset;
		textHelper(graphics,xStart,yStart,feathers,AQUA.colorToText());
	}
	@Override
	public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		int xStart = width / 2 + 10;
		int yStart = height - vOffset;
		//Draw feathers icon
		ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart + 82, yStart, 34, 0, 9, 9);
	}
}
