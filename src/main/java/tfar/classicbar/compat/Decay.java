package tfar.classicbar.compat;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class Decay {

  public static IDecayCapability getDecayHandler(EntityPlayer player){
   return player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
  }
}
