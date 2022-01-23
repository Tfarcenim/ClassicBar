package tfar.classicbar.overlays.mod;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.drawStringOnHUD;
import static tfar.classicbar.ModUtils.drawTexturedModalRect;
import static tfar.classicbar.ModUtils.getWidth;
import static tfar.classicbar.ModUtils.mc;
import static tfar.classicbar.ModUtils.rightTextOffset;

import com.elenai.elenaidodge2.ElenaiDodge2;
import com.elenai.elenaidodge2.api.FeathersHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

public class Feathers implements BarOverlay {

	private static final ResourceLocation DODGE_ICONS = new ResourceLocation(ElenaiDodge2.MODID, "textures/gui/icons.png");
	public boolean side;

	@Override
	public BarOverlay setSide(boolean side) {
		this.side = side;
		return this;
	}

	@Override
	public boolean rightHandSide() {
		return side;
	}

	@Override
	public boolean shouldRender(PlayerEntity player) {
		return true;
	}

	@Override
	public void renderBar(MatrixStack stack,PlayerEntity player, int width, int height) {
		double feathers = FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		int maxFeathers = 20;
		
		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		GlStateManager.enableBlend();

		Color.reset();
		//Bar background
		drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
		//draw portion of bar based on feathers amount
		int f = xStart + 79 - getWidth(feathers, maxFeathers);
		hex2Color("#22a5f0").color2Gl();
		drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(feathers, maxFeathers), 7);

	}

	@Override
	public boolean shouldRenderText() {
		return ModConfig.showHungerNumbers.get();
	}

	@Override
	public void renderText(MatrixStack stack,PlayerEntity player, int width, int height) {
		//draw feathers amount

		double feathers = FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		
		int h1 = (int) feathers;
		int c = Integer.decode("#22a5f0");
		if (ModConfig.showPercent.get()) h1 = (int) (feathers * 5);
		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		drawStringOnHUD(stack,h1 + "", xStart + 9 * ((ModConfig.displayIcons.get()) ? 1 : 0) + rightTextOffset, yStart - 1, c);
	}

	@Override
	public void renderIcon(MatrixStack stack,PlayerEntity player, int width, int height) {

		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		
		//Draw feathers icon
		mc.getTextureManager().bindTexture(DODGE_ICONS);
		GlStateManager.enableBlend();

		drawTexturedModalRect(stack,xStart + 82, yStart, 34, 0, 9, 9);
	}

	@Override
	public String name() {
		return "feathers";
	}
}
