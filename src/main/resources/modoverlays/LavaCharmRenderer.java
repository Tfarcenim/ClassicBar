package tfar.classicbar.overlays.modoverlays;

import lumien.randomthings.item.ItemLavaCharm;
import lumien.randomthings.item.ItemLavaWader;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
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
import tfar.classicbar.ClassicBar;
import tfar.classicbar.Color;
import tfar.classicbar.compat.BaublesHelper;

import static tfar.classicbar.ColorUtils.hex2Color;
import static tfar.classicbar.ModUtils.*;

/*
    Class handles the drawing of the lava charm*/
public class LavaCharmRenderer {
  private final Minecraft mc = Minecraft.getMinecraft();

  public static final Item Lava_Charm = ForgeRegistries.ITEMS.getValue(new ResourceLocation("randomthings:lavacharm"));
  public static final ResourceLocation ICON_LAVA = new ResourceLocation("randomthings", "textures/gui/lavacharmbar.png");

  public LavaCharmRenderer() {
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void renderLavaBar(RenderGameOverlayEvent.Pre event) {

    Entity renderViewEnity = mc.getRenderViewEntity();
    if (event.isCanceled()
            || !(renderViewEnity instanceof PlayerEntity)) return;
    PlayerEntity player = (PlayerEntity) renderViewEnity;
    if (player.capabilities.isCreativeMode) return;
    ItemStack stack = ItemStack.EMPTY;
    if (ClassicBar.BAUBLES)stack = BaublesHelper.getLavaWader(player);
    if (stack.isEmpty())stack = getLavaCharm(player);
    if (stack.isEmpty()) return;
    CompoundNBT nbt = stack.getTagCompound();
    if (nbt == null) {
      //System.out.println("error");
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
  public static ItemStack getLavaCharm(PlayerEntity player) {
    ItemStack stack1 = player.getItemStackFromSlot(EquipmentSlotType.FEET);
    if (isWader(stack1))return stack1;
    for (ItemStack stack : player.inventory.mainInventory)
      if (isCharm(stack)) return stack;
    return ItemStack.EMPTY;
  }
  private static boolean isCharm(ItemStack stack){
    return stack.getItem() instanceof ItemLavaCharm;
  }
  private static boolean isWader(ItemStack stack){
    return stack.getItem() instanceof ItemLavaWader;
  }
}