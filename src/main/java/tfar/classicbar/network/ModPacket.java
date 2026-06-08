package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;

public interface ModPacket {
    void encode(FriendlyByteBuf buf);
}
