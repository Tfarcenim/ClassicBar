import net.minecraft.util.Mth;
import tfar.classicbar.api.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Scratchpad {
    public static void main(String[] args) {
        List<Color> colorList = Stream.of("#D4AF37", "#C2C73B", "#8DC337", "#36BA77", "#4A5BC4",
                "#D89AE2", "#DF9DC7", "#DFA99D", "#D4DF9D", "#3E84C6", "#B8C1E8", "#DFDFDF").map(Color::hex2Color).toList();

        List<Color> grays = new ArrayList<>();

        for (Color color : colorList) {
            grays.add(blueTint(color));
        }
        for (Color color : grays) {
            System.out.println("\""+color.toHexString()+"\"");
        }
    }

    public static Color covertToGrayScale(Color color) {
        int r = color.r();
        int g = color.g();
        int b = color.b();
        int avg = (r+g+b)/3;
        return Color.fromRGB(avg,avg,avg);
    }

    public static Color greenTint(Color color) {
        int r = color.r();
        int g = color.g();
        int b = color.b();
        return new Color(0,Math.max(r-0x40,0),Math.min(g+0x40,0xff),Math.max(0,b-0x40));
    }


    public static Color blueTint(Color color) {
        int r = color.r();
        int g = color.g();
        int b = color.b();
        return new Color(0,Mth.clamp(r+0x20,0,0xff),
                Mth.clamp(g+0x20,0,0xff), Mth.clamp(b+0x80,0,0xff));
    }
}
