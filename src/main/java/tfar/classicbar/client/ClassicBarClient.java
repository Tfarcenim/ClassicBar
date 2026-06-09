package tfar.classicbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.classicbar.EventHandler;
import tfar.classicbar.network.PacketHandler;

public class ClassicBarClient {

    public static void init(IEventBus bus) {
        MinecraftForge.EVENT_BUS.addListener(ClassicBarClient::onClientPlayerLoggedIn);
        bus.addListener(EventHandler::setupOverlays);
        bus.addListener(EventHandler::sendModMessage);
        bus.addListener(ClassicBarClient::setup);
    }

    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(EventHandler::loadBarFiles);
    }

    public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
      Connection conn = event.getConnection();
      PacketHandler.presentOnServer = PacketHandler.channel().isRemotePresent(conn);
    }

    public static Player getLocalPlayer() {
      return Minecraft.getInstance().player;
    }
}
