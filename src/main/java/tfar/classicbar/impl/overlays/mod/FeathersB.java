package tfar.classicbar.impl.overlays.mod;

import com.elenai.feathers.api.FeathersHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarType;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.ModUtils;

public class FeathersB extends BarOverlayImpl{

	public static final BarInfo INFO = new BarInfo("feathers_feathers",ModCompat.feathers.name(),
			player -> true,player -> FeathersHelper.getFeathers(),
			player -> FeathersHelper.getMaxFeathers(), BarType.SINGLE);

	public FeathersB(BarSettings settings) {
		super(INFO,settings);
	}
	public static final ResourceLocation ICONS = new ResourceLocation("feathers", "textures/gui/icons.png");


	public static final Codec<FeathersB> CODEC = RecordCodecBuilder.create(inst ->
			codecStart(inst).apply(inst, FeathersB::new));

	@Override
	public Codec<? extends BarOverlayImpl> codec() {
		return CODEC;
	}

	@Override
	public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
		int xStart = width / 2 + getIconOffset();
		int yStart = height - vOffset;
		//Draw feathers icon
		ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 34, 0, 9, 9);
	}
}
