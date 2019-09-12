package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.Color;
import tfar.classicbar.compat.Decay;
import thebetweenlands.api.capability.IDecayCapability;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.overlays.modoverlays.TiaraBarRenderer.tiara;

/*
    Class handles the drawing of the thirst bar
 */

public class DecayRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation DECAY_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/decay_bar.png");

    public DecayRenderer() {
    }

    @SubscribeEvent//(priority = EventPriority.HIGH)
    public void renderThirstBar(RenderGameOverlayEvent.Post event) {
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (//event.getType() != RenderGameOverlayEvent.ElementType.AIR ||
                event.isCanceled() ||
                 !(renderViewEntity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) renderViewEntity;
        if (player.capabilities.isCreativeMode)return;
        IDecayCapability decayCap = Decay.getDecayHandler(player);
        double decay = decayCap.getDecayStats().getDecayLevel();
      //  System.out.println(thirstExhaustion);
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes

        int xStart = scaledWidth / 2 + 10;
        int yStart = scaledHeight - 49;

        mc.profiler.startSection("decay");
        if (ClassicBar.TOUGHASNAILS)yStart -= 10;
        if (player.getAir() < 300)yStart -= 10;
        if (Loader.isModLoaded("botania")&& BaublesApi.isBaubleEquipped(player, tiara) != -1)yStart -= 10;
        if (general.overlays.displayToughnessBar && player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()>= 1)yStart -= 10;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

        //draw portion of bar based on thirst amount

        float f = xStart+79-getWidth(decay,20);
        hex2Color("#81552D"/*mods.thirstBarColor*/).color2Gl();
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(decay,20), 7);

        //draw hydration if present
  /*      if (hydration>0){
            f = xStart + 79 - getWidth(hydration, 20);
            hex2Color((dehydration) ? mods.deHydrationSecondaryBarColor : mods.hydrationBarColor).color2Gl();
            drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(hydration,20), 7);
        }*/

        //draw thirst exhaustion
    /*    normalFractions = xStart - getWidth(thirstStats.getExhaustion(), 4) + 80;
        GlStateManager.color(1, 1, 1, .25f);
        drawTexturedModalRect(normalFractions, yStart + 1, 1, 28, getWidth(thirstStats.getExhaustion(), 4), 9);*/

       /* if (true/*general.overlays.hunger.showExhaustionOverlay) {
            thirstExhaustion = Math.min(thirstExhaustion,4);
            f = xStart - getWidth(thirstExhaustion, 4) + 80;
            //draw exhaustion
            GlStateManager.color(1, 1, 1, .25f);
            drawTexturedModalRect(f, yStart + 1, 1, 28, getWidth(thirstExhaustion, 4), 9);
        }*/

        //draw thirst amount
        int h1 = (int) Math.floor(decay);
        int c = Integer.decode("#81552D"/*mods.thirstBarColor*/);
        if (numbers.showPercent)h1 = (int)decay*5;
        if (numbers.showThirstNumbers)drawStringOnHUD(h1 + "", xStart + 9 * ((general.displayIcons) ? 1 : 0) + rightTextOffset, yStart - 1, c);
        //Reset back to normal settings
        Color.reset();

        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw thirst icon
            mc.getTextureManager().bindTexture(DECAY_BAR_TEXTURE);

            drawTexturedModalRect(xStart + 82, yStart, 18, 0, 9,9);
                drawTexturedModalRect(xStart + 82, yStart, 0, 0, 9, 9);
        }
        mc.getTextureManager().bindTexture(ICON_VANILLA);

        //GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
    }
}