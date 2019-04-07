package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.ModUtils.getStringLength;

/*
    Class handles the drawing of the health bar
 */

public class ArmorBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    private float alpha = 1;
    private boolean increase = false;
    public ArmorBarRenderer() {
    }

    @SubscribeEvent//(priority = EventPriority.LOW)
    public void renderArmorBar(RenderGameOverlayEvent.Pre event) {

        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.ARMOR
                || event.isCanceled()
                || !(renderViewEntity instanceof EntityPlayer)) {
            return;
        }
        event.setCanceled(true);
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        double armor = calculateArmorValue();
        if (armor < 1)return;
        boolean warning = false;
        for (ItemStack stack : player.inventory.armorInventory){
          int max = stack.getMaxDamage();
          int current = stack.getItemDamage();
          int percentage = 100;
          if (max !=0)percentage = 100*(max-current)/(max);
          if (percentage<5) warning = true;
        }
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes
      if (warning){
        if (increase)alpha+=.05;
        else alpha-=.05;
        if (alpha<0)increase=true;
        else if(alpha>1)increase=false;
      }else alpha=1;
        int absorb = MathHelper.ceil(player.getAbsorptionAmount());
      if (general.overlays.swap)absorb=0;
      int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - 49;
        if (absorb>0)yStart-=10;

        mc.profiler.startSection("armor");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background

        //Pass 1, draw bar portion

        //if armor >20
        if (armor<=20) {
          if (!general.overlays.fullArmorBar) drawScaledBar(armor, 20, xStart, yStart, true);
          else drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style,true,true);
          //calculate bar color
            cU.color2Gla(cU.hex2Color(colors.advancedColors.armorColorValues[0]),alpha);
            //draw portion of bar based on armor amount
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(armor,20), 7,general.style,true,true);
        }else {
          drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style,false,true);
          //we have wrapped, draw 2 bars
            int index = (int)Math.ceil(armor/20);
            int size = colors.advancedColors.armorColorValues.length;
            int i = index;
            //if we are out of colors wrap the bar
            if (index>=size) i=size-1;

            //draw first bar
            //case 1: bar is not capped and is partially filled
            if (index < size && armor % 20 != 0){
                //draw complete first bar
                cU.color2Gla(cU.hex2Color(colors.advancedColors.armorColorValues[i-1]),alpha);
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, 79, 7,general.style,true,true);

            //draw partial second bar
                cU.color2Gla(cU.hex2Color(colors.advancedColors.armorColorValues[i]),alpha);
            drawTexturedModalRect(xStart+1, yStart+1, 1, 10, getWidth(armor%20,20), 7,general.style,true,true);}
            //case 2, bar is a multiple of 20 or it is capped
            else{
                //draw complete second bar
                cU.color2Gl(cU.hex2Color(colors.advancedColors.armorColorValues[i]));
                drawTexturedModalRect(xStart+1, yStart+1, 1, 10, 79, 7,general.style,true,true);
            }
        }
        //draw armor amount
        int i1 = (int)Math.floor(armor);
        int i2 = getStringLength(i1+"");
        int i3 = (general.displayIcons)? 1 : 0;

        int c = Integer.decode(colors.advancedColors.armorColorValues[0]);
        if (numbers.showPercent)i1 = (int)armor*5;
        drawStringOnHUD(i1 + "", xStart - 9 * i3 - i2 + leftTextOffset, yStart - 1, c);
        //Reset back to normal settings

        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ICON_VANILLA);

        if(general.displayIcons)
        //Draw armor icon
        drawTexturedModalRect(xStart - 10, yStart, 43, 9, 9, 9,0,false,false);

        //armor icon
        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }
  private int calculateArmorValue()
  {
    int currentArmorValue = mc.player.getTotalArmorValue();

    for (ItemStack itemStack : mc.player.getArmorInventoryList())
    {
      if (itemStack.getItem() instanceof ISpecialArmor)
      {
        ISpecialArmor specialArmor = (ISpecialArmor) itemStack.getItem();
        currentArmorValue += specialArmor.getArmorDisplay(mc.player, itemStack, 0);
      }
    }
    return currentArmorValue;
  }
}
