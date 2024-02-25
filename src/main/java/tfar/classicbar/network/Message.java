package tfar.classicbar.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.classicbar.ClassicBar;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public final class Message {

  private static final String NETWORK_VERSION = "1.0";

  private static SimpleChannel channel;

  private static int id;

  public static boolean presentOnServer;

  public static SimpleChannel channel() {
    return channel;
  }

  public static void registerMessages(String channelName) {
    if (channel != null) return;
    channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ClassicBar.MODID, channelName),
            () -> NETWORK_VERSION,
            serverVersion -> NetworkRegistry.ABSENT.version().equals(serverVersion) || NETWORK_VERSION.equals(serverVersion),
            clientVersion -> NetworkRegistry.ABSENT.version().equals(clientVersion) || NETWORK_VERSION.equals(clientVersion)
    );
    channel.registerMessage(id++, MessageExhaustionSync.class,
            MessageExhaustionSync::encode,
            MessageExhaustionSync::new,
            MessageExhaustionSync::handle);

    channel.registerMessage(id++, MessageSaturationSync.class,
            MessageSaturationSync::encode,
            MessageSaturationSync::new,
            MessageSaturationSync::handle);

    channel.registerMessage(id++, MessageThirstExhaustionSync.class,
            MessageThirstExhaustionSync::encode,
            MessageThirstExhaustionSync::new,
            MessageThirstExhaustionSync::handle);

    channel.registerMessage(id++, MessageHydrationSync.class,
            MessageHydrationSync::encode,
            MessageHydrationSync::new,
            MessageHydrationSync::handle);
    EVENT_BUS.register(SyncHandler.instance());
  }

  private Message() {}

}
