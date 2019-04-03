package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class HealthBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private int updateCounter = 0;
    private double playerHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;

    private boolean forceUpdateIcons = false;

    public HealthBarRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHealthBar(RenderGameOverlayEvent.Pre event) {
        Entity renderViewEntity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTH
                || event.isCanceled()
                || !(renderViewEntity instanceof EntityPlayer)) return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes
        ;
        event.setCanceled(true);

        updateCounter = mc.ingameGUI.getUpdateCounter();

        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        double health = player.getHealth();
        boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

        if (health < playerHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 20);
        } else if (health > playerHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 10);
        }
        double absorb = player.getAbsorptionAmount();
        if (health != playerHealth || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        playerHealth = health;
        IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - 39;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("health");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int k5 = 16;

        if (player.isPotionActive(MobEffects.POISON)) k5 += 36;
        else if (player.isPotionActive(MobEffects.WITHER)) k5 += 72;

        int i4 = (highlight) ? 18 : 0;

        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9,general.style,true,true);

        //is the bar changing
        //Pass 1, draw bar portion

        //calculate bar color
        if (k5!=88)cU.color2Gl(cU.calculateScaledColor(health, maxHealth));
        else cU.color2Gl(cU.color2BW(cU.calculateScaledColor(health, maxHealth)));
        //draw portion of bar based on health remaining
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7,general.style,true,true);
        int h1 = (int) Math.ceil(health);

        //draw absorption bar if it exists
        if (absorb > 0) {
            if (general.overlays.swap) yStart -= 10;
            GlStateManager.color(1, 1, 1, 1);
            if (absorb <= maxHealth) {
                if (!general.overlays.fullAbsorptionBar) drawScaledBar(absorb, maxHealth, xStart, yStart - 10, true);
                else drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9,general.style,true,true);
                cU.color2Gl(cU.hex2Color(colors.advancedColors.absorptionColorValues[0]));
                drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb, maxHealth), 7,general.style,true,true);
            }
            else{
                //draw background bar
                drawTexturedModalRect(xStart, yStart - 10, 0, 0, 81, 9,general.style,true,true);
                //we have wrapped, draw 2 bars
                int index = (int)Math.floor(absorb/maxHealth);
                //don't crash from arrayindexoutofbounds
                if (index >= colors.advancedColors.absorptionColorValues.length - 1)
                    index = colors.advancedColors.absorptionColorValues.length - 1;
                //draw first full bar
                cU.color2Gl(cU.hex2Color(colors.advancedColors.absorptionColorValues[index-1]));
                drawTexturedModalRect(xStart+1,yStart - 9,1,10,79,7,general.style,true,true);
                //is it on the edge or capped already?
                if(absorb%maxHealth !=0 && index < colors.advancedColors.absorptionColorValues.length - 1) {
                    //draw second partial bar
                    cU.color2Gl(cU.hex2Color(colors.advancedColors.absorptionColorValues[index]));
                    drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb % maxHealth, maxHealth), 7,general.style,true,true);
                }
            }
            // handle the text
            int a1 = getStringLength((int) absorb + "");
            int a2 = general.displayIcons ? 1 : 0;
            int a3 = (int) absorb;
            int c = cU.colorToText(cU.hex2Color(colors.advancedColors.absorptionColorValues[0]));
            drawStringOnHUD(a3 + "", xStart - a1 - 9 * a2 - 5, yStart - 11, c);
            if (general.overlays.swap) yStart += 10;
        }
        int i1 = getStringLength(h1+"");
        int i2 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent)h1 = (int)(100*health/maxHealth);
        if (k5!=88)drawStringOnHUD(h1 +"", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, cU.colorToText(cU.calculateScaledColor(health,maxHealth)));
        else drawStringOnHUD(h1 +"", xStart - 9 * i2 - i1 + leftTextOffset, yStart - 1, cU.colorToText(cU.color2BW(cU.calculateScaledColor(health,maxHealth))));
        //Reset back to normal settings
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;
        if (absorb > 0) {
            GuiIngameForge.left_height += 10;
        }

        if (general.displayIcons) {
            int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
            //Draw health icon
            //heart background
            drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9,0,true,true);
            //heart
            drawTexturedModalRect(xStart - 10, yStart, 36+k5, 9 * i5, 9, 9,0,true,true);
            if (absorb>0){
              if (general.overlays.swap)yStart-=10;
              //draw absorption icon
                drawTexturedModalRect(xStart - 10, yStart - 10, 16, 9 * i5, 9, 9,0,true,true);
                drawTexturedModalRect(xStart - 10, yStart - 10, 160, 0, 9, 9,0,true,true);
            }
        }
        //Reset back to normal settings

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }


}
