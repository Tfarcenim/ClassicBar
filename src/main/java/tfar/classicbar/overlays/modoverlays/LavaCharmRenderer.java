package tfar.classicbar.overlays.modoverlays;

import lumien.randomthings.item.ItemLavaCharm;
import lumien.randomthings.item.ItemLavaWader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.Color;
import tfar.classicbar.compat.BaublesHelper;
import tfar.classicbar.overlays.IBarOverlay;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;

/*
    Class handles the drawing of the lava charm*/
public class LavaCharmRenderer implements IBarOverlay {

  @GameRegistry.ObjectHolder("randomthings:lavacharm")
  public static final Item lava_charm = null;

  public boolean side;

  @Override
  public boolean rightHandSide() {
    return side;
  }

  @Override
  public IBarOverlay setSide(boolean side) {
    this.side = side;
    return this;
  }

  public static final ResourceLocation ICON_LAVA = new ResourceLocation("randomthings", "textures/gui/lavacharmbar.png");

  @Override
  public boolean shouldRender(EntityPlayer player) {
    ItemStack stack = ItemStack.EMPTY;
    if (ClassicBar.BAUBLES)stack = BaublesHelper.getLavaWader(player);
    if (stack.isEmpty())stack = getLavaCharm(player);
    if (stack.isEmpty())return false;
    NBTTagCompound nbt = stack.getTagCompound();
    //proceeding will crash the game
    return nbt != null;
  }

  @Override
  public void renderBar(EntityPlayer player, int width, int height) {
    ItemStack stack = ItemStack.EMPTY;
    if (ClassicBar.BAUBLES)stack = BaublesHelper.getLavaWader(player);
    if (stack.isEmpty())stack = getLavaCharm(player);
    NBTTagCompound nbt = stack.getTagCompound();
    int charge = nbt.getInteger("charge");
    //Push to avoid lasting changes

    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    mc.profiler.startSection("charge");
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bar background
    Color.reset();
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9);

    //Pass 1, draw bar portion
    hex2Color(mods.lavaBarColor).color2Gl();
    //calculate bar color
    //draw portion of bar based on charge amount
    drawTexturedModalRect(xStart + 1, yStart + 1, 1, 10, getWidth(charge, 200), 7);

    Color.reset();
    GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

  @Override
  public boolean shouldRenderText() {
    return numbers.showLavaNumbers;
  }

  @Override
  public void renderText(EntityPlayer player, int width, int height) {
    int xStart = width / 2 - 91;
    int yStart = height - getSidedOffset();
    ItemStack stack = ItemStack.EMPTY;
    if (ClassicBar.BAUBLES)stack = BaublesHelper.getLavaWader(player);
    if (stack.isEmpty())stack = getLavaCharm(player);
    NBTTagCompound nbt = stack.getTagCompound();
    int charge = nbt.getInteger("charge");
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
    return "lavacharm";
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