package tfar.classicbar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.compat.ModCompat;
import toughasnails.api.thirst.ThirstHelper;

public record MessageThirstExhaustionSync(float exhaustion) implements CustomPacketPayload {

    public static final Type<MessageThirstExhaustionSync> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "thirst_exhaustion_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageThirstExhaustionSync> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.exhaustion),
                    buf -> new MessageThirstExhaustionSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageThirstExhaustionSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ModCompat.toughasnails.loaded && Minecraft.getInstance().player != null) {
                ThirstHelper.getThirst(Minecraft.getInstance().player).setExhaustion(msg.exhaustion);
            }
        });
    }
}
