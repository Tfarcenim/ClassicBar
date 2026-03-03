package tfar.classicbar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tfar.classicbar.ClassicBar;

public record MessageSaturationSync(float saturation) implements CustomPacketPayload {

    public static final Type<MessageSaturationSync> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "saturation_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageSaturationSync> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.saturation),
                    buf -> new MessageSaturationSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageSaturationSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getFoodData().setSaturation(msg.saturation);
            }
        });
    }
}
