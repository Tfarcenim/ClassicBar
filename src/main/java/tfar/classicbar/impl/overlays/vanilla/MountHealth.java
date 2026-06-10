package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.api.BarType;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.ModUtils;

public class MountHealth extends BarOverlayImpl {

  public static final BarInfo INFO = new BarInfo("mount_health",
          player -> player.getVehicle() instanceof LivingEntity livingEntity && livingEntity.isAlive()
          ,MountHealth::getNumerator,MountHealth::getDenominator, BarType.SINGLE);

  public MountHealth(BarSettings barSettings) {
    super(INFO,barSettings);
  }

  protected static float getNumerator(Player player) {
    LivingEntity mount = (LivingEntity) player.getVehicle();
      return mount.getHealth();
  }

  protected static float getDenominator(Player player) {
    LivingEntity mount = (LivingEntity) player.getVehicle();
      return mount.getMaxHealth();
  }

  public static final Codec<MountHealth> CODEC = RecordCodecBuilder.create(
          objectInstance -> codecStart(objectInstance).apply(objectInstance,MountHealth::new)
  );

  @Override
  public Codec<? extends BarOverlayImpl> codec() {
    return CODEC;
  }

  @Override
  public void renderIcon(GuiGraphics graphics, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //heart background
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 16, 0, 9, 9);
    //heart
    ModUtils.drawTexturedModalRect(getIconRL(),graphics,xStart, yStart, 88, 9, 9, 9);
  }
}