package tfar.classicbar;

import net.minecraft.client.renderer.GlStateManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tfar.classicbar.ScalingBarHandler.*;

/*
    Class representing the color of the armor icon
 */
public class HexColor
{
    public static float Red;
    public static float Blue;
    public static float Green;
    public static float Alpha;

    public HexColor()
    {
        Red = Blue = Green = Alpha = 1.0f;
    }

    /*
        Convert from #RRGGBB format.
        If string is not in correct format this function will set the color to black
     */
    public static void setColorFromHex(String colorHex)
    {
        //Check the color hex is valid
        Matcher matcher = p.matcher(colorHex);
        if (matcher.matches())
        {
            Red = Integer.valueOf(colorHex.substring(1, 3), 16).floatValue() / 255;
            Green = Integer.valueOf(colorHex.substring(3, 5), 16).floatValue() / 255;
            Blue = Integer.valueOf(colorHex.substring(5, 7), 16).floatValue() / 255;
        }
        else
        {
            //Set values to black (default minecraft color)
            Red = Blue = Green = 0;
        }
        GlStateManager.color(Red,Green,Blue);
    }
}