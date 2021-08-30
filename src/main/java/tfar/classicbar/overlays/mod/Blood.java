package tfar.classicbar.overlays.mod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

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
		return VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION;
	}

	@Override
	public void renderBar(MatrixStack stack,PlayerEntity player, int width, int height) {
		VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
			double blood = stats.getBloodLevel();
			//Push to avoid lasting changes
			int maxBlood = stats.getMaxBlood();

			int xStart = width / 2 + 10;
			int yStart = height - getSidedOffset();
			stack.push();
			GlStateManager.enableBlend();

			Color.reset();
			//Bar background
			drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
			//draw portion of bar based on blood amount
			int f = xStart + 79 - getWidth(blood, maxBlood);
			hex2Color("#FF0000"/*mods.thirstBarColor*/).color2Gl();
			drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(blood, maxBlood), 7);

			stack.pop();
		});
	}

	@Override
	public boolean shouldRenderText() {
		return ModConfig.showHungerNumbers.get();
	}

	@Override
	public void renderText(MatrixStack stack,PlayerEntity player, int width, int height) {
		//draw blood amount
		VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
					int blood = stats.getBloodLevel();

					int h1 = blood;
					int c = Integer.decode("#FF0000"/*mods.thirstBarColor*/);
					if (ModConfig.showPercent.get()) h1 = blood * 5;
					int xStart = width / 2 + 10;
					int yStart = height - getSidedOffset();
					drawStringOnHUD(stack,h1 + "", xStart + 9 * ((ModConfig.displayIcons.get()) ? 1 : 0) + rightTextOffset, yStart - 1, c);
		});

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

	@Override
	public String name() {
		return "blood";
	}
}
