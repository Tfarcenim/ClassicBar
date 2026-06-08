package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import tfar.classicbar.client.ClassicBarClient;

public record MessageSaturationSync(float saturationLevel) implements S2CModPacket {

    public MessageSaturationSync(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(saturationLevel);
    }

    public void handleClient() {
        ClassicBarClient.getLocalPlayer().getFoodData().setSaturation(saturationLevel);

    }
}
