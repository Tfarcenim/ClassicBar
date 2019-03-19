package tfar.classicbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

import static tfar.classicbar.config.ModConfig.numbers;

public class ModUtils {
    protected static final Field foodExhaustion = ReflectionHelper.findField(FoodStats.class, "foodExhaustionLevel", "field_75126_c", "c");


    public static final Item Lava_Charm = ForgeRegistries.ITEMS.getValue(new ResourceLocation("randomthings:lavacharm"));

    public static final ResourceLocation ICON_VANILLA = Gui.ICONS;
    public static final ResourceLocation ICON_BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static final FontRenderer fontRenderer = mc.fontRenderer;

    public static void drawTexturedModalRect(float x, float y, int textureX, int textureY, int width, int height) {
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public static int getWidth(double d1, double d2) {
        double d3 = 79 * d1 / d2;
        return (int) Math.ceil(d3);
    }

    public static int getStringLength(String s) {
        return fontRenderer.getStringWidth(s);
    }

    public static void drawStringOnHUD(String string, int xOffset, int yOffset, int color, int lineOffset) {
        yOffset += lineOffset * 9;
        if (numbers.showNumbers) fontRenderer.drawString(string, 2 + xOffset, 2 + yOffset, color, true);
    }

    public static float getExhaustion(EntityPlayer player) {
        float e1 = 0;
        try {
            e1 = foodExhaustion.getFloat(player.getFoodStats());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return e1;
    }

    public static void setExhaustion(EntityPlayer player, float exhaustion) {
        try {
            foodExhaustion.setFloat(player.getFoodStats(), exhaustion);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
