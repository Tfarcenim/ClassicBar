package tfar.classicbar.impl.overlays.mod;

import com.elenai.feathers.api.FeathersHelper;
import com.elenai.feathers.client.gui.FeathersHudOverlay;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ColorUtils;
import tfar.classicbar.util.ModUtils;

public class Feathers extends BarOverlayImpl {

	private static final ResourceLocation DODGE_ICONS = new ResourceLocation("elenaidodge2", "textures/gui/icons.png");

	public Feathers() {
		super("feathers");
	}

	@Override
	public boolean shouldRender(Player player) {
		return true;
	}

	@Override
	public void renderBar(ForgeGui gui, GuiGraphics graphics, Player player, int screenWidth, int screenHeight, int vOffset) {
		double feathers = FeathersHelper.getFeathers();
		int maxFeathers = FeathersHelper.getMaxFeathers();
		
		int xStart = screenWidth / 2 + 10;
		int yStart = screenHeight - vOffset;
		GlStateManager._enableBlend();

		Color.reset();
		//Bar background
		ModUtils.drawTexturedModalRect(graphics,xStart, yStart, 0, 0, 81, 9);
		//draw portion of bar based on feathers amount
		double f = xStart + 79 - ModUtils.getWidth(feathers, maxFeathers);
		ColorUtils.hex2Color("#22a5f0").color2Gl();
		ModUtils.drawTexturedModalRect(graphics,f, yStart + 1, 1, 10, ModUtils.getWidth(feathers, maxFeathers), 7);

	}

	@Override
	public void renderText(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		//draw feathers amount
		double feathers = 0;//FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		int c = Integer.decode("#22a5f0");
		int xStart = width / 2 + getIconOffset();
		int yStart = height - vOffset;
		textHelper(graphics,xStart,yStart,feathers,c);
	}
	@Override
	public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		int xStart = width / 2 + 10;
		int yStart = height - vOffset;
		//Draw feathers icon
		ModUtils.drawTexturedModalRect(graphics,xStart + 82, yStart, 34, 0, 9, 9);
	}
	@Override
	public double getBarWidth(Player player) {
		double feathers = FeathersHelper.getFeathers();
		int maxFeathers = FeathersHelper.getMaxFeathers();
		return WIDTH * feathers / maxFeathers;
	}
	@Override
	public ResourceLocation getIconRL() {
		return FeathersHudOverlay.ICONS;
	}
}
