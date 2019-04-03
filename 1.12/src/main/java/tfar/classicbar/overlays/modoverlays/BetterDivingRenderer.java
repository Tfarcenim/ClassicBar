package tfar.classicbar.overlays.modoverlays;

//import meldexun.betterDiving.capability.DivingAttributes;
//import meldexun.betterDiving.capability.DivingAttributesProvider;
//import meldexun.betterDiving.capability.IDivingAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

public class BetterDivingRenderer {

/*
    Class handles the drawing of the Better Diving oxygen bar
 */

//public DivingAttributesProvider divingAttributes = new DivingAttributesProvider();

    private final Minecraft mc = Minecraft.getMinecraft();

    public BetterDivingRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderBetterDivingBar(RenderGameOverlayEvent.Pre event) {
      if (true)throw new NullPointerException("This mod is incompatible!");
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      if (event.getType() != RenderGameOverlayEvent.ElementType.AIR
              || event.isCanceled()
              || !(renderViewEntity instanceof EntityPlayer)) {
        return;
      }
      double air = 0;
      event.setCanceled(true);
      EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();

      //double air = divingAttributes.getCapability(DivingAttributesProvider.DIVING,null);
      double maxAir = 3;
      if (air>=maxAir)return;
      int scaledWidth = event.getResolution().getScaledWidth();
      int scaledHeight = event.getResolution().getScaledHeight();
      //Push to avoid lasting changes
      int xStart = scaledWidth / 2 + 9;
      int yStart = scaledHeight - 49;
      if(general.overlays.displayToughnessBar && player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()>0)yStart-=10;
      if(Loader.isModLoaded("toughasnails"))yStart-=10;

      mc.profiler.startSection("air");
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();

      //Bind our Custom bar
      mc.getTextureManager().bindTexture(ICON_BAR);
      //Bar background
   //   drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style);
      //draw portion of bar based on air amount

      float f = xStart+80-getWidth(air,300);
      cU.color2Gl(cU.hex2Color(colors.oxygenBarColor));
 //     drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(air,maxAir), 7,general.style);

      //draw air amount
      int h1 = (int) Math.floor(air);

      int c = Integer.decode(colors.oxygenBarColor);
      int i3 = general.displayIcons ? 1 : 0;
      if (numbers.showPercent)h1 = (int)air/3;
      drawStringOnHUD(h1 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);
      //Reset back to normal settings

      GlStateManager.color(1, 1, 1, 1);

      mc.getTextureManager().bindTexture(ICON_VANILLA);
      GuiIngameForge.left_height += 10;

      if (general.displayIcons) {
        //Draw air icon
  //      drawTexturedModalRect(xStart + 82, yStart, 16, 18, 9, 9,0);
      }

      GlStateManager.disableBlend();
      //Revert our state back
      GlStateManager.popMatrix();
      mc.profiler.endSection();
      event.setCanceled(true);
    }

  }

