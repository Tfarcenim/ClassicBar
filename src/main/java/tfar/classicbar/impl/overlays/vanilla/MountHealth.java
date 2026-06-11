package tfar.classicbar.impl.overlays.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2i;
import tfar.classicbar.api.BarSettings;
import tfar.classicbar.impl.BarInfo;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.impl.IconData;

import java.util.List;

public class MountHealth extends BarOverlayImpl {

  public static final BarInfo INFO = BarInfo.getBuilder("mount_health")
          .setShouldRender(player -> player.getVehicle() instanceof LivingEntity livingEntity && livingEntity.isAlive())
          .setNumerator(MountHealth::getNumerator).setDenominator(MountHealth::getDenominator)
          .setIconData(new IconData(List.of(new Vector2i(16,0),new Vector2i(88,9))))
          .build();

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

}