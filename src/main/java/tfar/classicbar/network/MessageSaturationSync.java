package tfar.classicbar.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSaturationSync {
    private float saturationLevel;

    public MessageSaturationSync(){}

    public MessageSaturationSync(float saturationLevel)
    {
        this.saturationLevel = saturationLevel;
    }

    public MessageSaturationSync(PacketBuffer buf)
    {
        this.saturationLevel = buf.readFloat();
    }

    public void encode(PacketBuffer buf)
    {
        buf.writeFloat(saturationLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> NetworkHelper.getSidedPlayer(ctx.get()).getFoodStats().setFoodSaturationLevel(saturationLevel));
        ctx.get().setPacketHandled(true);
    }
}