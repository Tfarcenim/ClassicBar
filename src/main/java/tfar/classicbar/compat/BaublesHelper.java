package tfar.classicbar.compat;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static tfar.classicbar.overlays.modoverlays.LavaCharmRenderer.lava_charm;

public class BaublesHelper {
  public static ItemStack getLavaWader(EntityPlayer player){
    int i1 = BaublesApi.isBaubleEquipped(player, lava_charm);
    return i1 == -1 ? ItemStack.EMPTY : BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
  }
}
