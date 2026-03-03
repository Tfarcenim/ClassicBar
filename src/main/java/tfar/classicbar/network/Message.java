package tfar.classicbar.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.common.NeoForge;

public final class Message {

  public static boolean presentOnServer;

  public static void registerMessages(IEventBus modEventBus) {
    modEventBus.addListener(Message::onRegisterPayloads);
    NeoForge.EVENT_BUS.register(SyncHandler.instance());
  }

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
