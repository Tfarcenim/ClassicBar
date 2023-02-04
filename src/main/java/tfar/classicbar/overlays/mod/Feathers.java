package tfar.classicbar.overlays.mod;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ClassicBarsConfig;
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
	public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
		double feathers = 0;//FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		int maxFeathers = 20;
		
		int xStart = screenWidth / 2 + 10;
		int yStart = screenHeight - vOffset;
		GlStateManager._enableBlend();

		Color.reset();
		//Bar background
		ModUtils.drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
		//draw portion of bar based on feathers amount
		double f = xStart + 79 - ModUtils.getWidth(feathers, maxFeathers);
		ColorUtils.hex2Color("#22a5f0").color2Gl();
		ModUtils.drawTexturedModalRect(stack,f, yStart + 1, 1, 10, ModUtils.getWidth(feathers, maxFeathers), 7);

	}

	@Override
	public boolean shouldRenderText() {
		return ClassicBarsConfig.showHungerNumbers.get();
	}

	@Override
	public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
		//draw feathers amount
		double feathers = 0;//FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		int c = Integer.decode("#22a5f0");
		int xStart = width / 2 + getIconOffset();
		int yStart = height - vOffset;
		textHelper(stack,xStart,yStart,feathers,c);
	}
	@Override
	public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
		int xStart = width / 2 + 10;
		int yStart = height - vOffset;
		//Draw feathers icon
		ModUtils.drawTexturedModalRect(stack,xStart + 82, yStart, 34, 0, 9, 9);
	}
	@Override
	public ResourceLocation getIconRL() {
		return DODGE_ICONS;
	}
}
