package tfar.classicbar.compat;

import iblis.player.PlayerCharacteristics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class IblisHelper {
  public static double getMaxHunger(EntityPlayer player){
    return MathHelper.floor(PlayerCharacteristics.GLUTTONY.getCurrentValue(player));
  }
  public static double getCurrentHunger(EntityPlayer player){
    return player.getFoodStats().getFoodLevel();
  }
}
