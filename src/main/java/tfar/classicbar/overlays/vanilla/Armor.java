package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import tfar.classicbar.Color;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.drawTexturedModalRect;

public class Armor implements BarOverlay {

  private float armorAlpha = 1;
  private static final EquipmentSlotType[] armorList = new EquipmentSlotType[]{EquipmentSlotType.HEAD,
          EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

  public boolean side;

  @Override
  public BarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(PlayerEntity player) {
    return calculateArmorValue() >= 1;
  }

  @Override
  public void renderBar(MatrixStack stack, PlayerEntity player, int screenWidth, int screenHeight) {
    double armor = calculateArmorValue();
    int warningAmount = ModConfig.lowArmorWarning.get() ? getDamagedAmount(player) : 0;

    //Push to avoid lasting changes
    if (warningAmount > 0) armorAlpha = (int) (System.currentTimeMillis() / 250) % 2;

    int xStart = screenWidth / 2 - 91;
    int yStart = screenHeight - getSidedOffset();
    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();

    //how many layers are there? remember to start at 0
    int index = (int) Math.min(Math.ceil(armor / 20) - 1, ModConfig.armorColorValues.get().size() - 1);

    armor -= warningAmount;
    if (armor + warningAmount <= 20) {
      //bar background
      if (!ModConfig.fullArmorBar.get()) drawScaledBar(stack,armor + warningAmount, 20, xStart, yStart + 1, true);
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
    //armor icon
    RenderSystem.disableBlend();
    //Revert our state back
    RenderSystem.popMatrix();
  }

  @Override
  public boolean shouldRenderText() {
    return ModConfig.showArmorNumbers.get();
  }

  public static int getDamagedAmount(PlayerEntity player){
    int warningAmount = 0;
    for (EquipmentSlotType slot : armorList) {
      ItemStack stack = player.getItemStackFromSlot(slot);
      if (!(stack.getItem() instanceof ArmorItem)) continue;
      int max = stack.getMaxDamage();
      int current = stack.getDamage();
      int percentage = 100;
      if (max != 0) percentage = 100 * (max - current) / (max);
      if (percentage < 5) {
        warningAmount += ((ArmorItem) stack.getItem()).getArmorMaterial().getDamageReductionAmount(slot);
      }
    }
    return warningAmount;
  }

  @Override
  public void renderText(MatrixStack stack,PlayerEntity player, int width, int height) {
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
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
  public void renderIcon(MatrixStack stack,PlayerEntity player, int width, int height) {
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    Color.reset();
    //Draw armor icon
    drawTexturedModalRect(stack,xStart - 10, yStart, 43, 9, 9, 9);
  }

  @Override
  public String name() {
    return "armor";
  }

  private static int calculateArmorValue() {
    int currentArmorValue = mc.player.getTotalArmorValue();

   /* for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
      if (itemStack.getItem() instanceof ISpecialArmor) {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }*/
    return currentArmorValue;
  }

}