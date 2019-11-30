package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the tiara
 */
public class TiaraBarRenderer implements IBarOverlay {
  private final Minecraft mc = Minecraft.getMinecraft();

  @GameRegistry.ObjectHolder("botania:flighttiara")
  public static final Item tiara = null;
  private static final ResourceLocation ICON_BOTANIA = new ResourceLocation("botania", "textures/gui/hudicons.png");

  @Override
  public boolean shouldRender(EntityPlayer player) {
    int i1 = BaublesApi.isBaubleEquipped(player, tiara);
    if (i1 == -1) return false;
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();
    return nbt != null;
  }

  @Override
  public void render(EntityPlayer player,int width, int height) {

    int i1 = BaublesApi.isBaubleEquipped(player, tiara);
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();
    int timeLeft = nbt.getInteger("timeLeft");
    int dashCooldown = nbt.getInteger("dashCooldown");
    //Push to avoid lasting changes

    int xStart = width / 2 + 10;
    int yStart = height - GuiIngameForge.right_height;
    mc.profiler.startSection("flight");
    //GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    GlStateManager.color(1, 1, 1, 1);
    //draw main background
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);
    //draw dash background
    if (dashCooldown > 0) {
      int i4 = xStart - getWidth(dashCooldown, 80) + 81;
      drawTexturedModalRect(i4, yStart, 81 - getWidth(dashCooldown, 80), 18, getWidth(dashCooldown, 80), 9);
    }
    //Pass 1, draw bar portion
    hex2Color(mods.flightBarColor).color2Gl();
    //calculate bar color
    //draw portion of bar based on timeLeft amount
    float f = xStart + 79 - getWidth(timeLeft, 1200);
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(timeLeft, 1200), 7);
    int i2 = timeLeft / 20;
    //draw timeLeft amount
    if (numbers.showPercent) i2 = timeLeft / 12;
    int i3 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(mods.flightBarColor);
    if (numbers.showTiaraNumbers)drawStringOnHUD(i2 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);

    mc.getTextureManager().bindTexture(ICON_BOTANIA);
    Color.reset();
    if (general.displayIcons)
      //Draw flight icon
      drawTexturedModalRect(xStart + 81, yStart, Math.max(stack.getItemDamage() * 9 - 9, 0), 0, 9, 9);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);
    //GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public String name() {
    return "flighttiara";
  }
}