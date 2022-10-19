package tfar.classicbar.overlays.modoverlays;

import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeIngameGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import tfar.classicbar.util.Color;

import static tfar.classicbar.util.ColorUtils.hex2Color;
import static tfar.classicbar.util.ModUtils.*;

/*
    Class handles the drawing of the tiara
 */
public class TiaraBarRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();

  private static final Item tiara = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania:flighttiara"));
  private static final ResourceLocation ICON_BOTANIA = new ResourceLocation("botania", "textures/gui/hudicons.png");


  public TiaraBarRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void renderTiaraBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = mc.getRenderViewEntity();
    if (//event.isCanceled() ||
      //  event.getType() != RenderGameOverlayEvent.ElementType.HEALTH ||
            !(renderViewEnity instanceof PlayerEntity))
      return;

    PlayerEntity player = (PlayerEntity) mc.getRenderViewEntity();
    if (player.capabilities.isCreativeMode) return;
    int i1 = BaublesApi.isBaubleEquipped(player, tiara);
    if (i1 == -1) return;
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    CompoundNBT nbt = stack.getTagCompound();
    if (nbt == null) {
      System.out.println("error");
      return;
    }
    //System.out.println(nbt);
    int timeLeft = nbt.getInteger("timeLeft");
    int dashCooldown = nbt.getInteger("dashCooldown");
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes

    int xStart = scaledWidth / 2 + 10;
    int yStart = scaledHeight - 49;
    if (Loader.isModLoaded("toughasnails")) yStart -= 10;
    if (player.getAir() < 300) yStart -= 10;
    if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue() >= 1 && general.overlays.displayToughnessBar)
      yStart -= 10;
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
    drawStringOnHUD(i2 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);

    mc.getTextureManager().bindTexture(ICON_BOTANIA);
    Color.reset();
    if (general.displayIcons)
      //Draw flight icon
      drawTexturedModalRect(xStart + 81, yStart, Math.max(stack.getItemDamage() * 9 - 9, 0), 0, 9, 9);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    ForgeIngameGui.left_height += 10;
    //GlStateManager.disableBlend();
    //Revert our state back
    //GlStateManager.popMatrix();
    mc.profiler.endSection();
  }
}