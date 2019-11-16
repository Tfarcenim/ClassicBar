package tfar.classicbar.overlays.modoverlays;

import lumien.randomthings.item.ItemLavaCharm;
import lumien.randomthings.item.ItemLavaWader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.Color;
import tfar.classicbar.compat.BaublesHelper;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the lava charm*/
public class LavaCharmRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();

  public static final Item Lava_Charm = ForgeRegistries.ITEMS.getValue(new ResourceLocation("randomthings:lavacharm"));
  public static final ResourceLocation ICON_LAVA = new ResourceLocation("randomthings", "textures/gui/lavacharmbar.png");

  public LavaCharmRenderer() {
  }

  public void renderLavaBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = mc.getRenderViewEntity();
    if (event.getType() != RenderGameOverlayEvent.ElementType.ALL ||
             !(renderViewEnity instanceof EntityPlayer)) return;
    EntityPlayer player = (EntityPlayer) renderViewEnity;
    if (player.capabilities.isCreativeMode) return;
    ItemStack stack = ItemStack.EMPTY;
    if (ClassicBar.BAUBLES)stack = BaublesHelper.getLavaWader(player);
    if (stack.isEmpty())stack = getLavaCharm(player);
    if (stack.isEmpty()) return;
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) {
      //System.out.println("error");
      return;
    }
    int charge = nbt.getInteger("charge");
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes

    int xStart = scaledWidth / 2 - 91;
    int yStart = scaledHeight - GuiIngameForge.right_height;
    GuiIngameForge.right_height +=10;
    mc.profiler.startSection("charge");
    //GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //Pass 1, draw bar portion
    hex2Color(mods.lavaBarColor).color2Gl();
    //calculate bar color
    //draw portion of bar based on charge amount
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(charge, 200), 7);
    int i2 = charge/20;
    //draw charge amount
    if (numbers.showPercent) i2 /= 3;
    int i3 = getStringLength(i2 + "");
    int i4 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(mods.lavaBarColor);
    if (numbers.showLavaNumbers)drawStringOnHUD(i2 + "", xStart - 9 * i4 - i3 + leftTextOffset, yStart - 1, c);

    mc.getTextureManager().bindTexture(ICON_LAVA);
    Color.reset();

    if (general.displayIcons)
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
  public static ItemStack getLavaCharm(EntityPlayer player) {
    ItemStack stack1 = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
    return isWader(stack1) ? stack1 : player.inventory.mainInventory.stream().filter(LavaCharmRenderer::isCharm).findFirst().orElse(ItemStack.EMPTY);
  }
  private static boolean isCharm(ItemStack stack){
    return stack.getItem() instanceof ItemLavaCharm;
  }
  private static boolean isWader(ItemStack stack){
    return stack.getItem() instanceof ItemLavaWader;
  }
}