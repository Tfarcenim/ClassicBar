package tfar.classicbar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tfar.classicbar.ClassicBar;
// Changed (MC 26.1 upgrade): Tough As Nails compat is temporarily disabled because no
// 26.1 build of the mod is available, so toughasnails.* imports are removed. The packet is
// still registered to keep the network protocol stable; its handler is a no-op until the
// ToughAsNails integration is restored (see ModCompat/SyncHandler).

// Changed: completely rewritten from a plain class to a record implementing CustomPacketPayload.
// Old: separate constructor(FriendlyByteBuf), encode(FriendlyByteBuf), handle(Supplier<NetworkEvent.Context>),
//      registered via Message.channel.registerMessage() with an integer ID.
// New: record with TYPE + STREAM_CODEC; handle() uses IPayloadContext. ModCompat guard
//      and null check moved inside enqueueWork for safety.
public record MessageHydrationSync(float hydration) implements CustomPacketPayload {

    public static final Type<MessageHydrationSync> TYPE = // Changed: replaces integer channel ID
            new Type<>(Identifier.fromNamespaceAndPath(ClassicBar.MODID, "hydration_sync"));

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
        // No-op: Tough As Nails compat is disabled for MC 26.1. Restore the
        // ThirstHelper.getThirst(player).setHydration(msg.hydration()) call here once a
        // 26.1-compatible Tough As Nails build is available.
    }
}
