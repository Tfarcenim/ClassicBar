package tfar.classicbar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tfar.classicbar.ClassicBar;

public record MessageExhaustionSync(float exhaustion) implements CustomPacketPayload {

    public static final Type<MessageExhaustionSync> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "exhaustion_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageExhaustionSync> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.exhaustion),
                    buf -> new MessageExhaustionSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageExhaustionSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getFoodData().setExhaustion(msg.exhaustion);
            }
        });
    }
}
