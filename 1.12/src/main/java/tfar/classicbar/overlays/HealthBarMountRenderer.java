package tfar.classicbar.overlays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.ScalingBarHandler;

import static tfar.classicbar.HexColor.setColorFromHex;
import static tfar.classicbar.ScalingBarHandler.calculateBarHexColor;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the health bar
 */

public class HealthBarMountRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private int updateCounter = 0;
    private double mountHealth = 0;
    private double lasMountHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;

    private boolean forceUpdateIcons = false;

    public HealthBarMountRenderer() {
    }

    public void forceUpdate() {
        forceUpdateIcons = true;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHealthBar(RenderGameOverlayEvent.Pre event) {


        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (event.getType() != RenderGameOverlayEvent.ElementType.HEALTHMOUNT
                || event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) return;
        int scaledWidth = event.getResolution().getScaledWidth();
        int scaledHeight = event.getResolution().getScaledHeight();
        //Push to avoid lasting changes
        ;
        event.setCanceled(true);

        updateCounter = mc.ingameGUI.getUpdateCounter();

        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();

        if (player.getRidingEntity() == null)return;

        EntityLivingBase mount = (EntityLivingBase) player.getRidingEntity();
        if (mount.isDead)return;
        double mountHealth = mount.getHealth();


        boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

        if (mountHealth < this.mountHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 20);
        } else if (mountHealth > this.mountHealth && player.hurtResistantTime > 0) {
            lastSystemTime = Minecraft.getSystemTime();
            healthUpdateCounter = (long) (updateCounter + 10);
        }
        if (mountHealth != this.mountHealth || forceUpdateIcons) {
            forceUpdateIcons = false;
        }

        this.mountHealth = mountHealth;
        double j = lasMountHealth;
        IAttributeInstance maxHealthAttribute = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int xStart = scaledWidth / 2 + 9;
        int yStart = scaledHeight - 39;
        double maxHealth = maxHealthAttribute.getAttributeValue();

        mc.profiler.startSection("mountHealth");
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        int i4 = (highlight) ? 18 : 0;


        //Bind our Custom bar
        mc.getTextureManager().bindTexture(ICON_BAR);
        //Bar background
        drawTexturedModalRect(xStart, yStart, 0, i4, 81, 9);

        //is the bar changing
        //Pass 1, draw bar portion

        //calculate bar color
        setColorFromHex(calculateBarHexColor(mountHealth, maxHealth));
        float f = xStart+80-getWidth(mountHealth,maxHealth);
        //draw portion of bar based on mountHealth remaining
        drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(mountHealth, maxHealth), 7);
        //draw mountHealth amount
        int h1 = (int) Math.ceil(mountHealth);
        int h2 = (int) Math.ceil(maxHealth);

        int i3 = general.displayIcons ? 1 : 0;
        if (numbers.showPercent)h1 = (int)(100*mountHealth/maxHealth);
        drawStringOnHUD(h1+"", xStart + 100 - 9 * i3, yStart - 1, textColor(mountHealth/maxHealth), 0);

        //Reset back to normal settings
        GlStateManager.color(1, 1, 1, 1);

        mc.getTextureManager().bindTexture(ICON_VANILLA);
        GuiIngameForge.left_height += 10;

        if (general.displayIcons) {
            //Draw mountHealth icon
            //heart background
            drawTexturedModalRect(xStart + 82, yStart, 16, 0, 9, 9);
            //heart
            drawTexturedModalRect(xStart + 82, yStart, 88, 9, 9, 9);

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
