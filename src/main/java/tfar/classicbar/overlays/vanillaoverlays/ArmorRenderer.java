package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ISpecialArmor;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.drawTexturedModalRect;
import static tfar.classicbar.config.ModConfig.*;

public class ArmorRenderer implements IBarOverlay {

  private float armorAlpha = 1;
  private static EntityEquipmentSlot[] armorList = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD,
          EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

  public boolean side;

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return calculateArmorValue() >= 1;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {
    double armor = calculateArmorValue();
    int warningAmount = general.overlays.lowArmorWarning ? getDamagedAmount(player) : 0;

    //Push to avoid lasting changes
    if (warningAmount > 0) armorAlpha = (int) (Minecraft.getSystemTime() / 250) % 2;

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    mc.profiler.startSection("armor");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //how many layers are there? remember to start at 0
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, colors.advancedColors.armorColorValues.length - 1);

    armor -= warningAmount;
    if (armor + warningAmount <= 20) {
      //bar background
      if (!general.overlays.fullArmorBar) drawScaledBar(armor + warningAmount, 20, xStart, yStart, true);
      else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //calculate bar color
      hex2Color(colors.advancedColors.armorColorValues[0]).color2Gl();
      //draw portion of bar based on armor
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor, 20), 7);

      //draw damaged bar
      hex2Color(colors.advancedColors.armorColorValues[0]).color2Gla(armorAlpha);
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor + warningAmount, 20), 7);
    } else {
      //we have wrapped, draw 2 bars
      //bar background
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

      //draw first bar
      //case 1: bar is not capped and is partially filled
      if (warningAmount != 0 || index < colors.advancedColors.armorColorValues.length && (armor + warningAmount) % 20 != 0) {
        //draw complete first bar
        hex2Color(colors.advancedColors.armorColorValues[index - 1]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);

        //draw partial second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
      // now handle the low armor warning
      if (warningAmount > 0) {
        //armor and armor warning on same index
        if ((int) Math.ceil((warningAmount + armor) / 20) == (int) Math.ceil(armor / 20)) {
          //draw one bar
          hex2Color(colors.advancedColors.armorColorValues[index]).color2Gla(armorAlpha);
          drawTexturedModalRect(xStart + 1, yStart, 1, 10, getWidth(armor + warningAmount - index * 20, 20), 7);
        }
      }
    }

    //Reset back to normal settings

    Color.reset();
    //armor icon
    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showArmorNumbers;
  }

  public static int getDamagedAmount(EntityPlayer player){
    int warningAmount = 0;
    for (EntityEquipmentSlot slot : armorList) {
      ItemStack stack = player.getItemStackFromSlot(slot);
      if (!(stack.getItem() instanceof ItemArmor)) continue;
      int max = stack.getMaxDamage();
      int current = stack.getItemDamage();
      int percentage = 100;
      if (max != 0) percentage = 100 * (max - current) / (max);
      if (percentage < 5) {
        warningAmount += ((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(slot);
      }
    }
    return warningAmount;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    double armor = calculateArmorValue();
    //draw armor amount
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, colors.advancedColors.armorColorValues.length - 1);
    int warningAmount = getDamagedAmount(player);
    int i1 = (int) Math.floor(armor + warningAmount);
    int i3 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(colors.advancedColors.armorColorValues[index]);
    if (numbers.showPercent) i1 = (int) (armor + warningAmount) * 5;
    int i2 = getStringLength(i1 + "");
    drawStringOnHUD(i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {
    mc.getTextureManager().bindTexture(Gui.ICONS);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    Color.reset();
      //Draw armor icon
      drawTexturedModalRect(xStart - 10, yStart, 43, 9, 9, 9);
  }

  @Override
  public String name() {
    return "armor";
  }

  private static int calculateArmorValue() {
    int currentArmorValue = mc.player.getTotalArmorValue();

    for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
      if (itemStack.getItem() instanceof ISpecialArmor) {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }
    return currentArmorValue;
  }

}
