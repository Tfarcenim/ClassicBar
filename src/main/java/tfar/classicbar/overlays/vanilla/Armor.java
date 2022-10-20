package tfar.classicbar.overlays.vanilla;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;
import tfar.classicbar.util.ModUtils;

public class Armor  extends BarOverlayImpl {

  private float armorAlpha = 1;
  private static final EquipmentSlot[] armorList = new EquipmentSlot[]{EquipmentSlot.HEAD,
          EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

  public Armor() {
    super("armor");
  }

  @Override
  public boolean shouldRender(Player player) {
    return calculateArmorValue(player) >= 1;
  }

  @Override
  public void renderBar(ForgeIngameGui gui, PoseStack stack, Player player, int screenWidth, int screenHeight, int vOffset) {
    double armor = calculateArmorValue(player);
    double barWidth = getBarWidth(player);

    boolean warn = ClassicBarsConfig.lowArmorWarning.get() && getDamagedAmount(player) > 0;
    if (warn) armorAlpha = (int) (System.currentTimeMillis() / 250) % 2;
    int xStart = screenWidth / 2 + getHOffset();

    if (rightHandSide()) {
      xStart += ModUtils.WIDTH - barWidth;
    }

    int yStart = screenHeight - vOffset;
    //bar background
    renderBarBackground(stack,player,screenWidth,screenHeight,vOffset);
    //how many layers are there? remember to start at 0
    int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
    Color primary = getPrimaryBarColor(index, player);

    if (index == 0) {
      //calculate bar color
      primary.color2Gl();
      //draw portion of bar based on armor
      renderMainBar(stack,xStart , yStart , barWidth);
      //draw damaged bar
      primary.color2Gla(armorAlpha);
      ModUtils.drawTexturedModalRect(stack,xStart + 1, yStart + 1, 2, 10, barWidth, 7);
    } else {
      //we have wrapped, draw 2 bars
      //draw first bar
      //case 1: bar is not capped and is partially filled
      if ((armor) % 20 != 0) {
        Color secondary = getSecondaryBarColor(index-1, player);
        //draw complete first bar
        secondary.color2Gl();
        renderCompleteSecondaryBar(stack,xStart, yStart);
        //draw partial second bar
        primary.color2Gl();
        double f = xStart + (rightHandSide() ? ModUtils.WIDTH - ModUtils.getWidth(armor % 20, 20) : 0);
        renderMainBar(stack,f, yStart,ModUtils.getWidth(armor % 20, 20));
      }
      //case 2, bar is a multiple of 20, or it is capped
      else {
        //draw complete second bar
        primary.color2Gl();
        ModUtils.drawTexturedModalRect(stack,xStart + 1, yStart + 1, 1, 10, 79, 7);
      }
      // now handle the low armor warning
      if (warn) {
        //armor and armor warning on same index
        if ((int) Math.ceil((armor) / 20) == (int) Math.ceil(armor / 20)) {
          //draw one bar
          primary.color2Gla(armorAlpha);
          ModUtils.drawTexturedModalRect(stack,xStart + 1, yStart, 1, 10, ModUtils.getWidth(armor - index * 20, 20), 7);
        }
      }
    }
  }

  @Override
  public double getBarWidth(Player player) {
    int armor = calculateArmorValue(player);
    return Math.ceil(ModUtils.WIDTH * armor / 20d);
  }

  @Override
  public Color getPrimaryBarColor(int index, Player player) {
    return ConfigCache.armor.get(index);
  }

  @Override
  public Color getSecondaryBarColor(int index, Player player) {
    return ConfigCache.armor.get(index);
  }

  @Override
  public boolean isFitted() {
    return !ClassicBarsConfig.fullArmorBar.get();
  }

  @Override
  public boolean shouldRenderText() {
    return ClassicBarsConfig.showArmorNumbers.get();
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
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    double armor = calculateArmorValue(player);
    //draw armor amount
    int index = (int) Math.min(Math.ceil(armor / 20), ConfigCache.armor.size()) - 1;
    int c = getPrimaryBarColor(index, player).colorToText();
    textHelper(stack,xStart,yStart,armor,c);
  }

  @Override
  public void renderIcon(PoseStack stack, Player player, int width, int height, int vOffset) {
    int xStart = width / 2 + getIconOffset();
    int yStart = height - vOffset;
    //Draw armor icon
    ModUtils.drawTexturedModalRect(stack,xStart, yStart, 43, 9, 9, 9);
  }

  private static int calculateArmorValue(Player player) {
    int currentArmorValue = player.getArmorValue();

   /* for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
      if (itemStack.getItem() instanceof ISpecialArmor) {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }*/
    return currentArmorValue;
  }
}