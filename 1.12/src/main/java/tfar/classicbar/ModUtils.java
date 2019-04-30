package tfar.classicbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

import static tfar.classicbar.config.ModConfig.general;
import static tfar.classicbar.config.ModConfig.numbers;

public class ModUtils {
    public static final int rightTextOffset = 82;

    public static final int leftTextOffset = -5;

    protected static final Field foodExhaustion = ObfuscationReflectionHelper.findField(FoodStats.class,"field_75126_c");

    public static final ResourceLocation ICON_VANILLA = Gui.ICONS;
    public static final ResourceLocation ICON_BAR = new ResourceLocation(ClassicBar.MODID, "textures/gui/health.png");
    public static final Minecraft mc = Minecraft.getMinecraft();
    private static final FontRenderer fontRenderer = mc.fontRenderer;
    public static final int INDEX = 44;

    public static void drawTexturedModalRect(float x, float y, int textureX, int textureY, int width, int height,
                                             int style, boolean isBar, boolean left) {
        textureY += style*INDEX;
        if (isBar && !left)
        switch (style){
            case 1:{x -= 1;
            break;}
            default:
        }
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public static void drawPotential(float x, float y, int textureX, int textureY, int width, int height,
                                             int style) {
        textureY += style*INDEX;
            switch (style){
                case 1:{x -= 1;
                    break;}
                default:
            }
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }


    public static int getWidth(double d1, double d2) {
        int w = 79;
        if (general.style == 1)w-=1;
        double d3 = Math.max(w * d1 / d2,0);
        return (int) Math.ceil(d3);
    }

    public static int getStringLength(String s) {
        return fontRenderer.getStringWidth(s);
    }

    public static void drawScaledBar(double absorb, double maxHealth, float x, float y, boolean left) {
        int i = getWidth(absorb,maxHealth);
        switch (general.style){
            case 0: if (left) {

                drawScaledLeft(x, y, 0, 0, i + 1, 9);
                drawScaledLeft(x + i + 1, y + 1, 0, 1, 1, 7);
            }else{
                drawScaledLeft(x + 1, y, 0, 0, i + 1, 9);
                drawScaledLeft(x + i + 2, y + 1, 0, 1, 1, 7);}
                break;
            case 1: {
                i++;
                drawTexturedModalRect(x, y, 0, INDEX, i+1, 9,0,true,left);
                drawTexturedModalRect(x+i,y+1,1, INDEX + 1,1,7,0,true,left);
                drawTexturedModalRect(x+i+1,y+1,0, INDEX + 1,1,7,0,true,left);
                break;
            }

        }

    }
    public static void drawScaledLeft(float x, float y, int textureX, int textureY, int width, int height) {
        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public static void drawStringOnHUD(String string, int xOffset, int yOffset, int color) {
        if (!numbers.showNumbers)return;
        fontRenderer.drawString(string, 2 + xOffset, 2 + yOffset, color, true);
    }

    public static float getExhaustion(EntityPlayer player) {
        float e1;
        try {
            e1 = foodExhaustion.getFloat(player.getFoodStats());
        } catch (Exception e) {
            throw new RuntimeException(e);
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