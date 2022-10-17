package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSaturationSync {
    private float saturationLevel;

    public MessageSaturationSync(){}

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