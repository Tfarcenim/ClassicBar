package tfar.classicbar.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.common.NeoForge;

// Changed: entire networking system migrated from Forge SimpleChannel to NeoForge payload system.
// Old approach: NetworkRegistry.newSimpleChannel() created a named channel, each packet class
// had encode/decode/handle methods registered individually via channel.registerMessage() with an
// incrementing integer ID. New approach: each packet is a CustomPacketPayload record with a
// TYPE constant + STREAM_CODEC, registered via PayloadRegistrar.playToClient() in a
// RegisterPayloadHandlersEvent listener. No channel name or integer IDs needed.
public final class Message {

  public static boolean presentOnServer;

  // Changed: was registerMessages(String channelName) — now takes IEventBus directly.
  // Registers the payload handler listener on the mod bus and registers SyncHandler
  // on the NeoForge game bus (previously registered on MinecraftForge.EVENT_BUS).
  public static void registerMessages(IEventBus modEventBus) {
    modEventBus.addListener(Message::onRegisterPayloads);
    NeoForge.EVENT_BUS.register(SyncHandler.instance());
  }

  // Changed: replaces the old channel.registerMessage() calls. Each packet is now registered
  // with its TYPE (ResourceLocation) and STREAM_CODEC. playToClient() means server -> client only.
  // The registrar version string "1" serves as the network protocol version.
  private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");
    registrar.playToClient(
            MessageExhaustionSync.TYPE,
            MessageExhaustionSync.STREAM_CODEC,
            MessageExhaustionSync::handle);
    registrar.playToClient(
            MessageSaturationSync.TYPE,
            MessageSaturationSync.STREAM_CODEC,
            MessageSaturationSync::handle);
    registrar.playToClient(
            MessageThirstExhaustionSync.TYPE,
            MessageThirstExhaustionSync.STREAM_CODEC,
            MessageThirstExhaustionSync::handle);
    registrar.playToClient(
            MessageHydrationSync.TYPE,
            MessageHydrationSync.STREAM_CODEC,
            MessageHydrationSync::handle);
  }

  private Message() {}
}
