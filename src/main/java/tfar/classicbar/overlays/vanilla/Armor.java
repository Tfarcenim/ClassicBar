package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.impl.BarOverlayImpl;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.drawTexturedModalRect;

public class Armor  extends BarOverlayImpl {

  private float armorAlpha = 1;
  private static final EquipmentSlot[] armorList = new EquipmentSlot[]{EquipmentSlot.HEAD,
          EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

  public Armor() {
    super("armor");
  }

  @Override
  public boolean shouldRender(Player player) {
    return calculateArmorValue() >= 1;
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
    double armor = calculateArmorValue();
    int warningAmount = ModConfig.lowArmorWarning.get() ? getDamagedAmount(player) : 0;

    //Push to avoid lasting changes
    if (warningAmount > 0) armorAlpha = (int) (System.currentTimeMillis() / 250) % 2;

    int xStart = screenWidth / 2 + getHOffset();


    int yStart = screenHeight - vOffset;

    //how many layers are there? remember to start at 0
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, ModConfig.armorColorValues.get().size() - 1);

    armor -= warningAmount;
    if (armor + warningAmount <= 20) {
      //bar background
      if (!ModConfig.fullArmorBar.get()) drawScaledBar(stack,armor + warningAmount, 20, xStart, yStart + 1, rightHandSide());
      else drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);
      //calculate bar color
      hex2Color(ModConfig.armorColorValues.get().get(0)).color2Gl();
      //draw portion of bar based on armor
      drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, getWidth(armor, 20), 7);

      //draw damaged bar
      hex2Color(ModConfig.armorColorValues.get().get(0)).color2Gla(armorAlpha);
      drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, getWidth(armor + warningAmount, 20), 7);
    } else {
      //we have wrapped, draw 2 bars
      //bar background
      drawTexturedModalRect(stack,xStart, yStart, 0, 0, 81, 9);

      //draw first bar
      //case 1: bar is not capped and is partially filled
      if (warningAmount != 0 || index < ModConfig.armorColorValues.get().size() && (armor + warningAmount) % 20 != 0) {
        //draw complete first bar
        hex2Color(ModConfig.armorColorValues.get().get(index - 1)).color2Gl();
        drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, 79, 7);

        //draw partial second bar
        hex2Color(ModConfig.armorColorValues.get().get(index)).color2Gl();
        drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, getWidth(armor % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(ModConfig.armorColorValues.get().get(index)).color2Gl();
        drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
      // now handle the low armor warning
      if (warningAmount > 0) {
        //armor and armor warning on same index
        if ((int) Math.ceil((warningAmount + armor) / 20) == (int) Math.ceil(armor / 20)) {
          //draw one bar
          hex2Color(ModConfig.armorColorValues.get().get(index)).color2Gla(armorAlpha);
          drawTexturedModalRect(stack,xStart + 1, yStart, 1, 10, getWidth(armor + warningAmount - index * 20, 20), 7);
        }
      }
    }

    //Reset back to normal settings

    Color.reset();
  }

  @Override
  public boolean shouldRenderText() {
    return ModConfig.showArmorNumbers.get();
  }

  public static int getDamagedAmount(Player player){
    int warningAmount = 0;
    for (EquipmentSlot slot : armorList) {
      ItemStack stack = player.getItemBySlot(slot);
      if (!(stack.getItem() instanceof ArmorItem)) continue;
      int max = stack.getMaxDamage();
      int current = stack.getDamageValue();
      int percentage = 100;
      if (max != 0) percentage = 100 * (max - current) / (max);
      if (percentage < 5) {
        warningAmount += ((ArmorItem) stack.getItem()).getMaterial().getDefenseForSlot(slot);
      }
    }
    return warningAmount;
  }

  @Override
  public void renderText(PoseStack stack,Player player, int width, int height, int vOffset) {
    int xStart = width / 2 - 91;
    int yStart = height - vOffset;
    double armor = calculateArmorValue();
    //draw armor amount
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, ModConfig.armorColorValues.get().size() - 1);
    int warningAmount = getDamagedAmount(player);
    int i1 = (int) Math.floor(armor + warningAmount);
    int i3 = (ModConfig.displayIcons.get()) ? 1 : 0;
    int c = Integer.decode(ModConfig.armorColorValues.get().get(index));
    if (ModConfig.showPercent.get()) i1 = (int) (armor + warningAmount) * 5;
    int i2 = getStringLength(i1 + "");
    drawStringOnHUD(stack,i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 - 91;
    int yStart = height - vOffset;
    Color.reset();
    //Draw armor icon
    drawTexturedModalRect(stack,xStart - 10, yStart, 43, 9, 9, 9);
  }

  private static int calculateArmorValue() {
    int currentArmorValue = mc.player.getArmorValue();

   /* for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
      if (itemStack.getItem() instanceof ISpecialArmor) {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }*/
    return currentArmorValue;
  }

}