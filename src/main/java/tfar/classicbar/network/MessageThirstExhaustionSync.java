package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import tfar.classicbar.client.ClassicBarClient;
import tfar.classicbar.compat.ModCompat;
import toughasnails.api.thirst.ThirstHelper;

public record MessageThirstExhaustionSync(float exhaustionLevel) implements S2CModPacket {

    public MessageThirstExhaustionSync(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(exhaustionLevel);
    }

    public void handleClient() {
        if (ModCompat.toughasnails.loaded) {
            Player player = ClassicBarClient.getLocalPlayer();
            ThirstHelper.getThirst(player).setExhaustion(exhaustionLevel);
        }
    }
}
