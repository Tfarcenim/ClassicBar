package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.ModUtils;

public class Air extends BarOverlayImpl {

  public static final BarInfo INFO = BarInfo.getBuilder("air")
          .setShouldRender(player -> player.getAirSupply() < player.getMaxAirSupply())
          .setNumerator(Entity::getAirSupply)
          .setDenominator(Entity::getMaxAirSupply).build();

  public Air(BarSettings settings) {
    super(INFO,settings);
  }

  public static final Codec<Air> CODEC = RecordCodecBuilder.create(
          inst -> codecStart(inst).apply(inst,Air::new)
  );

  @Override
  public Codec<? extends BarOverlayImpl> codec() {
    return CODEC;
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //Draw air icon
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 16, 18, 9, 9);
  }
}