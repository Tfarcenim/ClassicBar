package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.ClassicBarClient;

public record MessageExhaustionSync(float exhaustionLevel) implements S2CModPacket {

    public MessageExhaustionSync(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(exhaustionLevel);
    }

    @Override
    public void handleClient() {
        Player player = ClassicBarClient.getLocalPlayer();
        player.getFoodData().setExhaustion(exhaustionLevel);
    }
}
