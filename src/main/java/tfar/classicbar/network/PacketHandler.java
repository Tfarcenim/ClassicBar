package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.classicbar.ClassicBar;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PacketHandler {

  private static final String NETWORK_VERSION = "2";

  private static SimpleChannel CHANNEL;

  private static int i;

  public static boolean presentOnServer;

  public static SimpleChannel channel() {
    return CHANNEL;
  }

  public static <MSG extends S2CModPacket > void sendToClient(MSG msg,ServerPlayer player) {
    CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
  }

  public static void registerMessages(String channelName) {
    if (CHANNEL != null) return;
    CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ClassicBar.MODID, channelName),
            () -> NETWORK_VERSION,
            serverVersion -> NetworkRegistry.ABSENT.version().equals(serverVersion) || NETWORK_VERSION.equals(serverVersion),
            clientVersion -> NetworkRegistry.ABSENT.version().equals(clientVersion) || NETWORK_VERSION.equals(clientVersion)
    );

    registerClientPacket(SyncState.S2CValueSync.class, SyncState.S2CValueSync::new);
  }

  public static  <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
    CHANNEL.registerMessage(i++, packetLocation, MSG::encode, reader, wrapS2C(), Optional.of(NetworkDirection.PLAY_TO_CLIENT));
  }

  public static <MSG extends S2CModPacket> BiConsumer<MSG, Supplier<NetworkEvent.Context>> wrapS2C() {
    return ((msg, contextSupplier) -> {
      contextSupplier.get().enqueueWork(msg::handleClient);
      contextSupplier.get().setPacketHandled(true);
    });
  }

  private PacketHandler() {}

}
