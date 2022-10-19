package tfar.classicbar.overlays.mod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.util.Color;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.util.ModUtils.*;

public class Blood extends BarOverlayImpl {

	private static final ResourceLocation VAMPIRISM_ICONS = new ResourceLocation("vampirism:textures/gui/icons.png");

	public Blood() {
		super("blood");
	}

	@Override
	public boolean shouldRender(Player player) {
		return VampirismAPI.factionRegistry().getFaction(player) == VReference.VAMPIRE_FACTION;
	}

	@Override
	public Color getPrimaryBarColor(int index, Player player) {
		return Color.RED;
	}

	@Override
	public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
		VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
			double blood = stats.getBloodLevel();
			//Push to avoid lasting changes
			int maxBlood = stats.getMaxBlood();

			int xStart = screenWidth / 2 + 10;
			int yStart = screenHeight - vOffset;
			stack.pushPose();
			GlStateManager._enableBlend();

			Color.reset();
			//Bar background
			drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
			//draw portion of bar based on blood amount
			double f = xStart + 79 - getWidth(blood, maxBlood);
			getSecondaryBarColor(0, player).color2Gl();
			drawTexturedModalRect(stack,f, yStart + 1, 1, 10, getWidth(blood, maxBlood), 7);

			stack.popPose();
		});
	}

	@Override
	public boolean shouldRenderText() {
		return ClassicBarsConfig.showHungerNumbers.get();
	}

	@Override
	public void renderText(PoseStack stack,Player player, int width, int height,int vOffset) {
		//draw blood amount
		VReference.VAMPIRE_FACTION.getPlayerCapability(player).map(IVampirePlayer::getBloodStats).ifPresent(stats -> {
					int blood = stats.getBloodLevel();

					int h1 = blood;
					int c = getPrimaryBarColor(0, player).colorToText();
					int xStart = width / 2 + getHOffset();
					int yStart = height - vOffset;
					drawStringOnHUD(stack,h1 + "", xStart + 9 * (ConfigCache.icons ? 1 : 0) + rightTextOffset, yStart - 1, c);
		});

	}

	@Override
	public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {

		int xStart = width / 2 + getHOffset();
		int yStart = height - vOffset;
		//Draw blood icon

		drawTexturedModalRect(stack,xStart + 82, yStart, 0, 0, 9, 9);
		drawTexturedModalRect(stack,xStart + 82, yStart, 9, 0, 9, 9);
	}

	@Override
	public ResourceLocation getIconRL() {
		return VAMPIRISM_ICONS;
	}

}
