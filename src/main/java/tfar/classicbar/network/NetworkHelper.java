package tfar.classicbar.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NetworkHelper {

    public static Player getPlayer(IPayloadContext ctx) {
        return ctx.player();
    }

    public static ServerPlayer getServerPlayer(IPayloadContext ctx) {
        Player player = ctx.player();
        if (player instanceof ServerPlayer sp) return sp;
        return null;
    }
}
