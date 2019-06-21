package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.Color;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.getStringLength;

/*
    Class handles the drawing of the health bar
 */

public class ArmorBarRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();
  private float alpha = 1;
  private static EntityEquipmentSlot[] list = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD,
          EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

  public ArmorBarRenderer() {
  }

  @SubscribeEvent//(priority = EventPriority.LOW)
  public void renderArmorBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEntity = mc.getRenderViewEntity();
    if (event.getType() != RenderGameOverlayEvent.ElementType.ARMOR
            || event.isCanceled()
            || !(renderViewEntity instanceof EntityPlayer)) {
      return;
    }
    event.setCanceled(true);
    EntityPlayer player = (EntityPlayer) renderViewEntity;
    double armor = calculateArmorValue();
    if (armor < 1) return;
    boolean warning = false;
    int warningAmount = 0;
    for (EntityEquipmentSlot slot : list) {
      if (!general.overlays.lowArmorWarning) break;
      ItemStack stack = player.getItemStackFromSlot(slot);
      int max = stack.getMaxDamage();
      int current = stack.getItemDamage();
      int percentage = 100;
      if (max != 0) percentage = 100 * (max - current) / (max);
      if (percentage < 90) {
        if (!(stack.getItem() instanceof ItemArmor)) continue;
        warning = true;
        warningAmount += ((ItemArmor) stack.getItem()).getArmorMaterial().getDamageReductionAmount(slot);
      }
    }
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes
    if (warning && general.overlays.lowArmorWarning) alpha = (int) (Minecraft.getSystemTime() / 250) % 2;
    int absorb = MathHelper.ceil(player.getAbsorptionAmount());
    if (general.overlays.swap) absorb = 0;
    int xStart = scaledWidth / 2 - 91;
    int yStart = scaledHeight - 49;
    if (absorb > 0) yStart -= 10;
    mc.profiler.startSection("armor");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background

    //Pass 1, draw bar portion
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, colors.advancedColors.armorColorValues.length - 1);

    armor -= warningAmount;
    //if armor >20
    if (armor + warningAmount <= 20) {
      //bar background
      if (!general.overlays.fullArmorBar) drawScaledBar(armor + warningAmount, 20, xStart, yStart + 1, true);
      else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //calculate bar color
      hex2Color(colors.advancedColors.armorColorValues[0]).color2Gl();
      //draw portion of bar based on armor
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor, 20), 7);

      //draw damaged bar
      hex2Color(colors.advancedColors.armorColorValues[0]).color2Gla(alpha);
      drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor + warningAmount, 20), 7);
    } else {
      drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
      //we have wrapped, draw 2 bars
      int size = colors.advancedColors.armorColorValues.length;
      //if we are out of colors wrap the bar

      //draw first bar
      //case 1: bar is not capped and is partially filled
      if (index < (size - 1) && armor % 20 != 0) {
        //draw complete first bar
        hex2Color(colors.advancedColors.armorColorValues[index - 1]).color2Gla(alpha);
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);

        //draw partial second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gla(alpha);
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor % 20, 20), 7);
      }
      //case 2, bar is a multiple of 20 or it is capped
      else {
        //draw complete second bar
        hex2Color(colors.advancedColors.armorColorValues[index]).color2Gl();
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
    }
    //draw armor amount
    int i1 = (int) Math.floor(armor + warningAmount);
    int i3 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(colors.advancedColors.armorColorValues[index]);
    if (numbers.showPercent) i1 = (int) (armor + warningAmount) * 5;
    int i2 = getStringLength(i1+"");
    drawStringOnHUD(i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
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
    event.setCanceled(true);
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
