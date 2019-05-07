package tfar.classicbar.overlays.modoverlays;

import lumien.randomthings.item.ItemLavaCharm;
import lumien.randomthings.item.ItemLavaWader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the lava charm
 */
public class LavaCharmNoBaublesRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();

    public static final Item Lava_Charm = ForgeRegistries.ITEMS.getValue(new ResourceLocation("randomthings:lavacharm"));
    public static final ResourceLocation ICON_LAVA = new ResourceLocation("randomthings", "textures/gui/lavacharmbar.png");

    public LavaCharmNoBaublesRenderer() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderLavaBar(RenderGameOverlayEvent.Pre event) {

        Entity renderViewEnity = mc.getRenderViewEntity();
        if (event.isCanceled()
                || !(renderViewEnity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
        if (player.capabilities.isCreativeMode)return;

        ItemStack stack = getLavaCharm(player);
        if (stack == null)return;

        NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                System.out.println("error");
                return;
            }
        int charge = nbt.getInteger("charge");
            int scaledWidth = event.getResolution().getScaledWidth();
            int scaledHeight = event.getResolution().getScaledHeight();
            //Push to avoid lasting changes

            int absorb = MathHelper.ceil(player.getAbsorptionAmount());

            int xStart = scaledWidth / 2 - 91;
            int yStart = scaledHeight - 49;
            if (absorb > 0) yStart -= 10;
            if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue() >= 1) yStart -= 10;
            mc.profiler.startSection("charge");
            //GlStateManager.pushMatrix();
            GlStateManager.enableBlend();

            //Bind our Custom bar
            mc.getTextureManager().bindTexture(ICON_BAR);
            //Bar background
        GlStateManager.color(1, 1, 1,1);
        drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9,general.style,false,true);

            //Pass 1, draw bar portion
        hex2Color(mods.lavaBarColor).color2Gl();
            //calculate bar color
            //draw portion of bar based on charge amount
            drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(charge, 200), 7,general.style,true,true);
        int i2 = charge/20;
            //draw charge amount
        if (numbers.showPercent)i2 /= 3;
        int i3 = getStringLength(i2+"");
            int i4 = (general.displayIcons) ? 1 : 0;
        int c = Integer.decode(mods.lavaBarColor);
            drawStringOnHUD(i2 + "", xStart - 9 * i4 - i3 + leftTextOffset, yStart - 1, c);

            mc.getTextureManager().bindTexture(ICON_LAVA);
        GlStateManager.color(1, 1, 1, 1);

            if (general.displayIcons)
                //Draw charge icon
                drawTexturedModalRect(xStart - 10, yStart, 1, 1, 9, 9,0,false,true);
        //Reset back to normal settings

            mc.getTextureManager().bindTexture(ICON_VANILLA);

        GuiIngameForge.left_height += 10;
         //GlStateManager.disableBlend();
            //Revert our state back
            //GlStateManager.popMatrix();
            mc.profiler.endSection();
        }

    public static ItemStack getLavaCharm(EntityPlayer player) {
        ItemStack stack1 = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (isWader(stack1))return stack1;
        for (ItemStack stack : player.inventory.mainInventory)
            if (isCharm(stack)) return stack;
        return null;
    }
    private static boolean isCharm(ItemStack stack){
        return stack.getItem() instanceof ItemLavaCharm;
    }
    private static boolean isWader(ItemStack stack){
        return stack.getItem() instanceof ItemLavaWader;
    }
}