package tfar.classicbar.overlays.vanillaoverlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.GuiIngameForge;
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

  @Override
  public boolean shouldRender(EntityPlayer player) {
    return calculateArmorValue() >= 1;
  }

  @Override
  public void render(EntityPlayer player, int width, int height) {
    double armor = calculateArmorValue();
    boolean warning = false;
    int warningAmount = 0;
    for (EntityEquipmentSlot slot : armorList) {
      if (!general.overlays.lowArmorWarning) break;
      ItemStack stack = player.getItemStackFromSlot(slot);
      int max = stack.getMaxDamage();
      int current = stack.getItemDamage();
      int percentage = 100;
      if (max != 0) percentage = 100 * (max - current) / (max);
      if (percentage < 5) {
        if (!(stack.getItem() instanceof ItemArmor)) continue;
        warning = true;
        warningAmount += ((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(slot);
      }
    }

    //Push to avoid lasting changes
    if (warning && general.overlays.lowArmorWarning) armorAlpha = (int) (Minecraft.getSystemTime() / 250) % 2;

    int xStart = width / 2 - 91;
    int yStart = height - GuiIngameForge.left_height;
    mc.profiler.startSection("armor");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background

    //Pass 1, draw bar portion
    //how many layers are there? remember to start at 0
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, colors.advancedColors.armorColorValues.length - 1);

    armor -= warningAmount;
    //if armor >20
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
      drawTexturedModalRect(xStart + 1, yStart, 1, 10, getWidth(armor + warningAmount, 20), 7);
    } else {
      //we have wrapped, draw 2 bars
      //bar background
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

      //draw first bar
      //case 1: bar is not capped and is partially filled
      if (warningAmount != 0 || index < colors.advancedColors.armorColorValues.length && (armor + warningAmount) % 20 != 0) {
        //draw complete first bar
        hex2Color(colors.advancedColors.armorColorValues[index - 1]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart, 1, 10, 79, 7);

        //draw partial second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart, 1, 10, getWidth(armor % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart, 1, 10, 79, 7);
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
    //draw armor amount
    int i1 = (int) Math.floor(armor + warningAmount);
    int i3 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(colors.advancedColors.armorColorValues[index]);
    if (numbers.showPercent) i1 = (int) (armor + warningAmount) * 5;
    int i2 = getStringLength(i1 + "");
    if (numbers.showArmorNumbers) drawStringOnHUD(i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
    //Reset back to normal settings

    Color.reset();

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    if (general.displayIcons)
      //Draw armor icon
      drawTexturedModalRect(xStart - 10, yStart, 43, 9, 9, 9);

    //armor icon
    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "armor";
  }

  private int calculateArmorValue() {
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
