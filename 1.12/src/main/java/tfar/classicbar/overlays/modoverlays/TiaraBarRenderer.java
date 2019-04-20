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
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static tfar.classicbar.ColorUtilities.cU;
import static tfar.classicbar.ModUtils.*;
import static tfar.classicbar.config.ModConfig.*;
/*
    Class handles the drawing of the tiara
 */
public class TiaraBarRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();


  @Config.Name("Botania Options")
  public static ConfigBotania configBotania = new ConfigBotania();

  public static class ConfigBotania {
    @Config.Name("Flight Bar Color")
    public String flightBarColor = "#FFFFFF";
  }

  private static final Item tiara = ForgeRegistries.ITEMS.getValue(new ResourceLocation("botania:flighttiara"));
  private static final ResourceLocation ICON_BOTANIA = new ResourceLocation("botania", "textures/gui/hudicons.png");


  public TiaraBarRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void renderTiaraBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = this.mc.getRenderViewEntity();
    if (//event.isCanceled() ||
          //  event.getType() != RenderGameOverlayEvent.ElementType.HEALTH ||
            !(renderViewEnity instanceof EntityPlayer))
      return;

    EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
    if (player.capabilities.isCreativeMode) return;
    int i1 = BaublesApi.isBaubleEquipped(player, tiara);
    if (i1 == -1) return;
    ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(i1);
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) {
      System.out.println("error");
      return;
    }
    int timeLeft = nbt.getInteger("timeLeft");
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();
    //Push to avoid lasting changes

    int xStart = scaledWidth / 2 + 10;
    int yStart = scaledHeight - 49;
    if (Loader.isModLoaded("toughasnails")) yStart -= 10;
    if (player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue()>=1)yStart -=10;
    if (player.getAir()<300)yStart -=10;
    mc.profiler.startSection("flight");
    //GlStateManager.pushMatrix();
    GlStateManager.enableBlend();

    //Bind our Custom bar
    mc.getTextureManager().bindTexture(ICON_BAR);
    //Bar background
    GlStateManager.color(1, 1, 1, 1);
    drawTexturedModalRect(xStart, yStart, 0, 0, 81, 9, general.style, false, false);

    //Pass 1, draw bar portion
    cU.color2Gl(cU.hex2Color(configBotania.flightBarColor));
    //calculate bar color
    //draw portion of bar based on timeLeft amount
    float f = xStart+80-getWidth(timeLeft,1200);
    drawTexturedModalRect(f, yStart + 1, 1, 10, getWidth(timeLeft, 1200), 7, general.style, true, false);
    int i2 = timeLeft;
    //draw timeLeft amount
    if (numbers.showPercent) i2 /= 3;
    int i3 = (general.displayIcons) ? 1 : 0;
    int c = Integer.decode(configBotania.flightBarColor);
    drawStringOnHUD(i2 + "", xStart + 9 * i3 + rightTextOffset, yStart - 1, c);

    mc.getTextureManager().bindTexture(ICON_BOTANIA);
    GlStateManager.color(1, 1, 1, 1);

    if (general.displayIcons)
      //Draw timeLeft icon
      drawTexturedModalRect(xStart + 82, yStart, 0, 0, 9, 9, 0, false, false);
    //Reset back to normal settings

    mc.getTextureManager().bindTexture(ICON_VANILLA);

    GuiIngameForge.left_height += 10;
    //GlStateManager.disableBlend();
    //Revert our state back
    //GlStateManager.popMatrix();
    mc.profiler.endSection();
  }
}