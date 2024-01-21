package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSaturationSync {

    private final float saturationLevel;

    public MessageSaturationSync(float saturationLevel)
    {
        this.saturationLevel = saturationLevel;
    }

    public MessageSaturationSync(FriendlyByteBuf buf)
    {
        this.saturationLevel = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeFloat(saturationLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodData().setSaturation(saturationLevel));
        ctx.get().setPacketHandled(true);
    }

}
