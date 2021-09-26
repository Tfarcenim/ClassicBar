package tfar.classicbar.overlays.mod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

import java.util.Optional;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;

public class Blood implements BarOverlay {

	private static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");
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
		return VampirismAPI.getFactionPlayerHandler(player).map(a -> a.isInFaction(VReference.VAMPIRE_FACTION)).orElse(false);
	}

	@Override
	public void renderBar(MatrixStack stack,PlayerEntity player, int width, int height) {
		int bloodWidth = getBloodStats(player).map(stats -> getWidth(stats.getBloodLevel(), stats.getMaxBlood())).orElse(0);

		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();

		Color.reset();
		//Bar background
		drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
		//draw portion of bar based on blood amount
		int f = xStart + 79 - bloodWidth;
		hex2Color("#FF0000"/*mods.thirstBarColor*/).color2Gl();
		drawTexturedModalRect(stack,f, yStart + 1, 1, 10, bloodWidth, 7);

		RenderSystem.popMatrix();
	}

	@Override
	public boolean shouldRenderText() {
		return ModConfig.showHungerNumbers.get();
	}

	@Override
	public void renderText(MatrixStack stack,PlayerEntity player, int width, int height) {
		//draw blood amount
		int blood = getBloodStats(player).map(IBloodStats::getBloodLevel).orElse(0);

		int h1 = blood;
		int c = Integer.decode("#FF0000"/*mods.thirstBarColor*/);
		if (ModConfig.showPercent.get()) h1 = blood * 5;
		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		drawStringOnHUD(stack,h1 + "", xStart + 9 * ((ModConfig.displayIcons.get()) ? 1 : 0) + rightTextOffset, yStart - 1, c);
	}

	@Override
	public void renderIcon(MatrixStack stack,PlayerEntity player, int width, int height) {

		int xStart = width / 2 + 10;
		int yStart = height - getSidedOffset();
		//Draw blood icon
		mc.getTextureManager().bindTexture(VAMPIRISM_ICONS);
		GlStateManager.enableBlend();

		drawTexturedModalRect(stack,xStart + 82, yStart, 0, 0, 9, 9);
		drawTexturedModalRect(stack,xStart + 82, yStart, 9, 0, 9, 9);
	}

	/**
	 * only call if player is vampire
	 */
	private Optional<IBloodStats> getBloodStats(PlayerEntity player) {
		return VampirismAPI.getFactionPlayerHandler(player).map(IFactionPlayerHandler::getCurrentFactionPlayer).orElse(Optional.empty()).map(a -> ((IVampirePlayer) a).getBloodStats());
	}

	@Override
	public String name() {
		return "blood";
	}
}
