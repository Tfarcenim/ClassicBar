package tfar.classicbar.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import tfar.classicbar.compat.ModCompat;
import toughasnails.api.thirst.ThirstHelper;

import java.util.function.Supplier;

public class MessageThirstExhaustionSync {

    private final float exhaustionLevel;

    public MessageThirstExhaustionSync(float exhaustionLevel) {
        this.exhaustionLevel = exhaustionLevel;
    }

    public MessageThirstExhaustionSync(FriendlyByteBuf buf) {
        this.exhaustionLevel = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(exhaustionLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ModCompat.toughasnails.loaded) {
            ctx.get().enqueueWork(() -> {
                Player player = NetworkHelper.getSidedPlayer(ctx.get());
                ThirstHelper.getThirst(player).setExhaustion(exhaustionLevel);
            });
        }
        ctx.get().setPacketHandled(true);
    }

}
