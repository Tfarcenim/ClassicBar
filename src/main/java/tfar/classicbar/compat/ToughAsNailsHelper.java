package tfar.classicbar.compat;

import net.minecraft.entity.player.EntityPlayer;
import toughasnails.api.TANCapabilities;
import toughasnails.thirst.ThirstHandler;

public class ToughAsNailsHelper {
  public static ThirstHandler getHandler(EntityPlayer player) {
    return (ThirstHandler) player.getCapability(TANCapabilities.THIRST, null);
  }
}
