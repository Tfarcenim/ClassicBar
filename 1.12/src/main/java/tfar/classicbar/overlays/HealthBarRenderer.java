package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static java.lang.Math.floor;
import static tfar.classicbar.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class HealthBarRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private int updateCounter = 0;
    private double playerHealth = 0;
    private double lastPlayerHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;

    private boolean forceUpdateIcons = false;

    public HealthBarRenderer() {
    }

    public void forceUpdate() {
        forceUpdateIcons = true;
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
        double j = lastPlayerHealth;
        IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 - 91;
        int yStart = scaledHeight - 39;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("health");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int k5 = 16;
        int i4 = (highlight) ? 18 : 0;


        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

        //is the bar changing
        //Pass 1, draw bar portion

        //calculate bar color
        calculateBarColor(health, maxHealth);
        //draw portion of bar based on health remaining
        drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(health, maxHealth), 7);
        //draw health amount
        int h1 = (int) Math.ceil(health);
        int h2 = (int) Math.ceil(maxHealth);

        //draw absorption bar if it exists
        if (absorb > 0) {
            GlStateManager.color(1, 1, 1, 1);
            if (!fullAbsorptionBar)drawScaledBar(absorb, maxHealth,xStart,yStart - 10);
            else drawTexturedModalRect(xStart, yStart - 10, 0, i4, 81, 9);

            int a1 = getStringLength((int)absorb+"");
            GlStateManager.color(0.831f, 0.686f, 0.215f, 1);
            drawTexturedModalRect(xStart + 1, yStart - 9, 1, 10, getWidth(absorb, maxHealth), 7);
            int a2 = displayIcons ? 1 : 0;
            int a3 = (int)absorb;
            drawStringOnHUD(a3 + "", xStart  - a1 - 9 * a2 - 5, yStart - 11, 0xD4AF37, 0);
        }
        int i1 = getStringLength(h1+"");
        int i2 = displayIcons ? 1 : 0;
        if (showPercent)h1 = (int)(100*health/maxHealth);
        drawStringOnHUD(h1 +"", xStart - 9 * i2 - i1 - 5, yStart - 1, textColor(health/maxHealth), 0);

        //Reset back to normal settings
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;
        if (absorb > 0) {
            GuiIngameForge.left_height += 10;
        }

        if (displayIcons) {
            int i5 = (player.world.getWorldInfo().isHardcoreModeEnabled()) ? 5 : 0;
            //Draw health icon
            //heart background
            drawTexturedModalRect(xStart - 10, yStart, 16, 9 * i5, 9, 9);
            //heart
            drawTexturedModalRect(xStart - 10, yStart, 52, 9 * i5, 9, 9);
            if (absorb>0){
                //draw absorption icon
                drawTexturedModalRect(xStart - 10, yStart - 10, 16, 9 * i5, 9, 9);
                drawTexturedModalRect(xStart - 10, yStart - 10, 160, 0, 9, 9);
            }

        }

        //Reset back to normal settings

        GlStateManager.disableBlend();
        //Revert our state back
        GlStateManager.popMatrix();
        mc.profiler.endSection();
        event.setCanceled(true);
    }

    private void drawScaledBar(double absorb, double maxHealth, float x, float y) {
        int i = getWidth(absorb,maxHealth)+1;
        drawTexturedModalRect(x, y, 0, 0, i, 9);
        drawTexturedModalRect(x+i,y+1,0,1,1,7);

    }
    private void calculateBarColor(double health, double maxHealth) {
        float f = (float) (health / maxHealth);
        float g = MathHelper.clamp(4 * f - 1, 0, 1);
        float r = Math.min(1, 2 * (1 - f));
        GlStateManager.color(r, g, 0, 1);
    }
    private int textColor(double d1){
        if (d1>=.5){
            int r = (int)Math.floor(0xFF * 2 * (1 - d1));
            r *= 0x10000;
            return r+0x00FF00;
        }
        if (d1>=.25) {
            int g = (int)Math.floor(4 * 0xFF * (d1 - 1));
            g *= 0x100;
            return g+0xFF0000;}
        return 0xFF0000;
    }
}
