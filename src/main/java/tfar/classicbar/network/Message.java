package tfar.classicbar.network;


import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.classicbar.ClassicBar;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;


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

    INSTANCE.registerMessage(ID++, MessageThirstExhaustionSync.class,
            MessageThirstExhaustionSync::encode,
            MessageThirstExhaustionSync::new,
            MessageThirstExhaustionSync::handle);

    INSTANCE.registerMessage(ID++, MessageHydrationSync.class,
            MessageHydrationSync::encode,
            MessageHydrationSync::new,
            MessageHydrationSync::handle);
    EVENT_BUS.register(new SyncHandler());
  }

}
