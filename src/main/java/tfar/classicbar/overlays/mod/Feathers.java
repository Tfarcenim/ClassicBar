package tfar.classicbar.overlays.mod;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;

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
	public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
		//draw feathers amount

		double feathers = 0;//FeathersHelper.getFeatherLevel(Minecraft.getInstance().player);
		
		int h1 = (int) feathers;
		int c = Integer.decode("#22a5f0");
		if (ModConfig.showPercent.get()) h1 = (int) (feathers * 5);
		int xStart = width / 2 + getHOffset();
		int yStart = height - vOffset;
		drawStringOnHUD(stack,h1 + "", xStart + 9 * ((ModConfig.displayIcons.get()) ? 1 : 0) + rightTextOffset, yStart - 1, c);
	}

	@Override
	public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {

		int xStart = width / 2 + 10;
		int yStart = height - vOffset;
		
		//Draw feathers icon
		drawTexturedModalRect(stack,xStart + 82, yStart, 34, 0, 9, 9);
	}

	@Override
	public ResourceLocation getIconRL() {
		return DODGE_ICONS;
	}
}
