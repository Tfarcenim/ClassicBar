package tfar.classicbar.network;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.classicbar.ClassicBar;


public class Message {

  public static SimpleChannel INSTANCE;

  public static int ID;

  public static void registerMessages(String channelName) {
    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ClassicBar.MODID, channelName), () -> "1.0", s -> true, s -> true);
    INSTANCE.registerMessage(ID++, MessageExhaustionSync.class,
            MessageExhaustionSync::encode,
            MessageExhaustionSync::new,
            MessageExhaustionSync::handle);

    INSTANCE.registerMessage(ID++, MessageSaturationSync.class,
            MessageSaturationSync::encode,
            MessageSaturationSync::new,
            MessageSaturationSync::handle);

  }
}