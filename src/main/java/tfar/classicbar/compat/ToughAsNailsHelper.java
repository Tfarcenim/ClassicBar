package tfar.classicbar.compat;

import net.minecraft.entity.player.EntityPlayer;
import toughasnails.api.TANCapabilities;
import toughasnails.api.stat.capability.IThirst;
import toughasnails.thirst.ThirstHandler;

public class ToughAsNailsHelper {
  public static IThirst getHandler(EntityPlayer player) {
    return player.getCapability(TANCapabilities.THIRST, null);
  }
}
