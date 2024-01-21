package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import tfar.classicbar.compat.Helpers;
import toughasnails.api.thirst.ThirstHelper;

import java.util.function.Supplier;

public class MessageHydrationSync {

    private final float hydrationLevel;

    public MessageHydrationSync(float hydrationLevel) {
        this.hydrationLevel = hydrationLevel;
    }

    public MessageHydrationSync(FriendlyByteBuf buf) {
        this.hydrationLevel = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(hydrationLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (Helpers.toughasnailsLoaded) {
            ctx.get().enqueueWork(() -> {
                Player player = NetworkHelper.getSidedPlayer(ctx.get());
                ThirstHelper.getThirst(player).setHydration(hydrationLevel);
            });
        }
        ctx.get().setPacketHandled(true);
    }

}
