package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tfar.classicbar.impl.BarOverlayImpl;
import tfar.classicbar.util.Color;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;
import static tfar.classicbar.overlays.modoverlays.LavaCharmRenderer.ICON_LAVA;

/*
    Class handles the drawing of the lava charm*/
public class LavaWaderBaubleRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();
  public static final Item LavaWaderBauble = ForgeRegistries.ITEMS.getValue(new ResourceLocation("lavawaderbauble:lavawaderbauble"));

  public LavaWaderBaubleRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void renderLavaBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = mc.getRenderViewEntity();
    if (event.isCanceled()
            || !(renderViewEnity instanceof PlayerEntity)) {
      return;
    }
    PlayerEntity player = (PlayerEntity) renderViewEnity;
    if (player.capabilities.isCreativeMode) return;
    int i1 = BaublesApi.isBaubleEquipped(player, LavaWaderBauble);
    if (i1 == -1) return;
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    CompoundNBT nbt = stack.getTagCompound();
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
    mc.getTextureManager().bindTexture(BarOverlayImpl.ICON_BAR);
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
    drawStringOnHUD(i2 + "", xStart - 9 * i4 - i3 + leftTextOffset, yStart - 1, c);

    mc.getTextureManager().bindTexture(ICON_LAVA);
    Color.reset();

    if (general.displayIcons)
      //Draw charge icon
      drawTexturedModalRect(xStart - 10, yStart, 1, 1, 9, 9);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    ForgeIngameGui.left_height += 10;
    //GlStateManager.disableBlend();
    //Revert our state back
    //GlStateManager.popMatrix();
    mc.profiler.endSection();
  }

}