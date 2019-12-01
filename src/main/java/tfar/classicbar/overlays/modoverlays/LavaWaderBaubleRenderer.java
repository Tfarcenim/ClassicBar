package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import tfar.classicbar.Color;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
import static tfar.classicbar.overlays.modoverlays.LavaCharmRenderer.ICON_LAVA;

/*
    Class handles the drawing of the lava charm*/
public class LavaWaderBaubleRenderer implements IBarOverlay {

  @GameRegistry.ObjectHolder("lavawaderbauble:lavawaderbauble")
  public static final Item LavaWaderBauble = null;

  public boolean side;

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public boolean shouldRender(EntityPlayer player) {
    int i1 = BaublesApi.isBaubleEquipped(player, LavaWaderBauble);
    if (i1 == -1) return false;
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();
    return nbt != null;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {

    int i1 = BaublesApi.isBaubleEquipped(player, LavaWaderBauble);
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();

    int charge = nbt.getInteger("charge");
    //Push to avoid lasting changes

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    mc.profiler.startSection("charge");
    //GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //Pass 1, draw bar portion
    hex2Color(mods.lavaBarColor).color2Gl();
    //calculate bar color
    //draw portion of bar based on charge amount
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(charge, 200), 7);

    //Reset back to normal settings

    GlStateManager.disableBlend();
    //Revert our state back
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return true;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {

    int i1 = BaublesApi.isBaubleEquipped(player, LavaWaderBauble);
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();

    int charge = nbt.getInteger("charge");
    //Push to avoid lasting changes

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    int i2 = charge/20;
    //draw charge amount
    if (numbers.showPercent) i2 /= 3;
    int i3 = getStringLength(i2 + "");
    int i4 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(mods.lavaBarColor);
    drawStringOnHUD(i2 + "", xStart - 9 * i4 - i3 + leftTextOffset, yStart - 1, c);
  }

  @Override
  public void renderIcon(EntityPlayer player, int width, int height) {
    mc.getTextureManager().bindTexture(ICON_LAVA);
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();

    drawTexturedModalRect(xStart - 10, yStart, 1, 1, 9, 9);

  }

  @Override
  public String name() {
    return "lavawader2";
  }
}