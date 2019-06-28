package tfar.classicbar.config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;

import static tfar.classicbar.ColorUtils.p1;
import static tfar.classicbar.config.ModConfig.*;

public class IdiotHandler {
    public static IdiotHandler idiots = new IdiotHandler();

    public static TextFormatting color = TextFormatting.RED;

    public IdiotHandler() {
    }
    public void idiotsTryingToParseBadHexColorsDOTJpeg() {
        //TODO add idiot proofing for mods
        colors.hungerBarColor = isSomeoneAttemptingToAddABadHexCodeToTheConfigQuestionMark(colors.hungerBarColor);
        colors.oxygenBarColor = isSomeoneAttemptingToAddABadHexCodeToTheConfigQuestionMark(colors.oxygenBarColor);
        colors.saturationBarColor = isSomeoneAttemptingToAddABadHexCodeToTheConfigQuestionMark(colors.saturationBarColor);
    }

    public String isSomeoneAttemptingToAddABadHexCodeToTheConfigQuestionMark(String s) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = mc.player;
        Matcher m = p1.matcher(s);
        if (m.matches()) return s;
        if (p != null)p.sendMessage(new TextComponentString(color + "USER PUT IN A BAD HEX CODE"));
        return "#000000";
    }

    public void emptyArrayFixer() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = mc.player;
        if (colors.advancedColors.normalFractions.length == 0) {
            p.sendMessage(new TextComponentString(color + "USER PUT IN AN EMPTY ARRAY FOR HEALTH SCALING"));
            if (colors.advancedColors.normalColors.length > 0) {
                colors.advancedColors.normalFractions = new double[colors.advancedColors.normalColors.length];
                for (double i1 = 0; i1 < colors.advancedColors.normalColors.length; i1++) {
                    colors.advancedColors.normalFractions[(int) i1] = (float) (i1 / colors.advancedColors.normalColors.length);
                }
                return;
            } else {
                p.sendMessage(new TextComponentString(color + "USER PUT IN AN EMPTY ARRAY FOR HEALTH COLOR SCALING"));
                colors.advancedColors.normalFractions = new double[]{.25, .5, .75};
                colors.advancedColors.normalColors = new String[]{"#FF0000", "#FFFF00", "#00FF00"};
                return;
            }
        }
        if (colors.advancedColors.normalColors.length == 0) {
            p.sendMessage(new TextComponentString(color + "USER PUT IN AN EMPTY ARRAY FOR HEALTH COLOR SCALING"));
            colors.advancedColors.normalColors = new String[colors.advancedColors.normalColors.length];
            for (double i1 = 0; i1 < colors.advancedColors.normalFractions.length; i1++)
                colors.advancedColors.normalColors[(int) i1] = "#000000";
        }
    }
}
