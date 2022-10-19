package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageExhaustionSync {
    private float exhaustionLevel;

    public MessageExhaustionSync() {}

    public MessageExhaustionSync(float exhaustionLevel) {
        this.exhaustionLevel = exhaustionLevel;
    }

    public MessageExhaustionSync(FriendlyByteBuf buf) {
        this.exhaustionLevel = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(exhaustionLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        {
            // defer to the next game loop; we can't guarantee that Minecraft.thePlayer is initialized yet
            ctx.get().enqueueWork(() -> {

                Player player = NetworkHelper.getSidedPlayer(ctx.get());

                player.getFoodData().setExhaustion(exhaustionLevel);
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
