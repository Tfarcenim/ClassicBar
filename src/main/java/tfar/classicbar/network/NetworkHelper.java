package tfar.classicbar.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// Changed: completely rewritten for NeoForge networking.
// Old: getSidedPlayer(NetworkEvent.Context) checked NetworkDirection to return either the
// sending ServerPlayer (server side) or Minecraft.getInstance().player (client side).
// New: IPayloadContext.player() already returns the correct side-appropriate player,
// so the direction check is gone. getServerPlayer() added as a typed helper for
// server-side handlers that need to send replies or access server-only APIs.
public final class NetworkHelper {
    private NetworkHelper() {} // §16: utility class — private no-arg constructor

    // Changed: replaces getSidedPlayer(NetworkEvent.Context); no direction check needed
    // because NeoForge's IPayloadContext.player() is already side-appropriate.
    public static Player getPlayer(IPayloadContext ctx) {
        return ctx.player();
    }

    // Changed: new helper; safe cast for handlers that only run on the server side.
    // Returns null if called on the client (player is not a ServerPlayer).
    public static ServerPlayer getServerPlayer(IPayloadContext ctx) {
        Player player = ctx.player();
        if (player instanceof ServerPlayer sp) return sp;
        return null;
    }
}
