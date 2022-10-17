package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.classicbar.ModUtils;

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
            ctx.get().enqueueWork(() -> ModUtils.setExhaustion(NetworkHelper.getSidedPlayer(ctx.get()), exhaustionLevel));
        }
        ctx.get().setPacketHandled(true);
    }
}
