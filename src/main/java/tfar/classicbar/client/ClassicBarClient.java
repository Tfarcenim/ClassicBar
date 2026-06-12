package tfar.classicbar.client;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.EventHandler;
import tfar.classicbar.network.PacketHandler;

public class ClassicBarClient {

    public static void init(IEventBus bus) {
        MinecraftForge.EVENT_BUS.addListener(ClassicBarClient::onClientPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(ClassicBarClient::commands);
        bus.addListener(EventHandler::setupOverlays);
        bus.addListener(EventHandler::sendModMessage);
        bus.addListener(ClassicBarClient::setup);
    }

    public static void setup(FMLClientSetupEvent event) {
    }

    static void commands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(ClassicBar.MODID)
                .then(Commands.literal("reload")
                        .executes(c -> {
                            EventHandler.cacheConfigs();
                            return 1;
                        })
                )
        );
    }

    public static void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
      Connection conn = event.getConnection();
      PacketHandler.presentOnServer = PacketHandler.channel().isRemotePresent(conn);
    }

    public static Player getLocalPlayer() {
      return Minecraft.getInstance().player;
    }
}
