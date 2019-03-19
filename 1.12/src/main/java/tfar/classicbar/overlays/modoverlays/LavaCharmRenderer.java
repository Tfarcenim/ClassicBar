package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import lumien.randomthings.item.ItemLavaWader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static tfar.classicbar.ModConfig.displayIcons;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the lava charm
 */

public class LavaCharmRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    private int updateCounter = 0;
    private double playerLava = 1;

    private static final ResourceLocation ICON_LAVA = new ResourceLocation("randomthings", "textures/gui/lavacharmbar.png");

    private boolean forceUpdateIcons = false;

    public LavaCharmRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderLavaBar(RenderGameOverlayEvent.Pre event) {

        Entity renderViewEnity = this.mc.getRenderViewEntity();
        if (event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        int i1 = BaublesApi.isBaubleEquipped(player, Lava_Charm);

        if (i1 == -1) {
            if (!(player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemLavaWader))return;}
            //System.out.println(i1);
        int charge;
        if (i1 != -1) {ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                System.out.println("error");
                return;
            }
            charge = nbt.getInteger("charge");}
            else {charge = player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getTagCompound().getInteger("charge");}
            int scaledWidth = event.getResolution().getScaledWidth();
            int scaledHeight = event.getResolution().getScaledHeight();
            //Push to avoid lasting changes

            updateCounter = mc.ingameGUI.getUpdateCounter();
            int absorb = MathHelper.ceil(player.getAbsorptionAmount());

            if (charge != playerLava || forceUpdateIcons) {
                forceUpdateIcons = false;
            }

            playerLava = charge;
            int xStart = scaledWidth / 2 - 91;
            int yStart = scaledHeight - 49;
            if (absorb > 0) yStart -= 10;
            if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue() > 0) yStart -= 10;
            mc.profiler.startSection("charge");
            //GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            //Bind our Custom bar
            mc.getTextureManager().bindTexture(ICON_BAR);
            //Bar background
        GlStateManager.color(1, 1, 1,1);
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

            //Pass 1, draw bar portion
            GlStateManager.color(1, .5f, 0,1);
            //calculate bar color
            //draw portion of bar based on charge amount
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(charge, 200), 7);

            //draw charge amount
            int i3 = getStringLength(charge+"");
            int i4 = (displayIcons) ? 1 : 0;
            int c = 0xFF7700;

            drawStringOnHUD(charge + "", xStart - 9 * i4 - i3 - 5, yStart - 1, c, 0);

            mc.getTextureManager().bindTexture(ICON_LAVA);
        GlStateManager.color(1, 1, 1, 1);

            if (displayIcons)
                //Draw charge icon
                drawTexturedModalRect(xStart - 10, yStart, 1, 1, 9, 9);
        //Reset back to normal settings

            mc.getTextureManager().bindTexture(ICON_VANILLA);

        GuiIngameForge.left_height += 10;
         //GlStateManager.disableBlend();
            //Revert our state back
            //GlStateManager.popMatrix();
            mc.profiler.endSection();
        }
    }
