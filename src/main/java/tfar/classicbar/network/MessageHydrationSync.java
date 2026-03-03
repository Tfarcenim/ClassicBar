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

public record MessageHydrationSync(float hydration) implements CustomPacketPayload {

    public static final Type<MessageHydrationSync> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "hydration_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageHydrationSync> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.hydration),
                    buf -> new MessageHydrationSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MessageHydrationSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ModCompat.toughasnails.loaded && Minecraft.getInstance().player != null) {
                ThirstHelper.getThirst(Minecraft.getInstance().player).setHydration(msg.hydration);
            }
        });
    }
}
