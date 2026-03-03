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

// Changed: completely rewritten from a plain class to a record implementing CustomPacketPayload.
// Old: separate constructor(FriendlyByteBuf), encode(FriendlyByteBuf), handle(Supplier<NetworkEvent.Context>),
//      registered via Message.channel.registerMessage() with an integer ID.
// New: record with TYPE + STREAM_CODEC; handle() uses IPayloadContext. ModCompat guard
//      and null check moved inside enqueueWork for safety.
public record MessageHydrationSync(float hydration) implements CustomPacketPayload {

    public static final Type<MessageHydrationSync> TYPE = // Changed: replaces integer channel ID
            new Type<>(ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "hydration_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageHydrationSync> STREAM_CODEC = // Changed: replaces separate encode() and decode-constructor
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.hydration),
                    buf -> new MessageHydrationSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Changed: replaces handle(Supplier<NetworkEvent.Context>). Old version obtained player via
    // NetworkHelper.getSidedPlayer(); now uses Minecraft.getInstance().player directly.
    // ctx.get().setPacketHandled(true) removed — NeoForge marks packets handled automatically.
    public static void handle(MessageHydrationSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ModCompat.toughasnails.loaded && Minecraft.getInstance().player != null) {
                ThirstHelper.getThirst(Minecraft.getInstance().player).setHydration(msg.hydration);
            }
        });
    }
}
