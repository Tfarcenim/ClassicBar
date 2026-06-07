package tfar.classicbar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tfar.classicbar.ClassicBar;

// Changed: completely rewritten from a plain class to a record implementing CustomPacketPayload.
// Old: separate constructor(FriendlyByteBuf), encode(FriendlyByteBuf), handle(Supplier<NetworkEvent.Context>),
//      and a separate matching registration in Message.channel.registerMessage() with an int ID.
// New: record holds the payload field directly; TYPE provides the packet identifier;
//      STREAM_CODEC replaces the encode/decode pair; handle() uses IPayloadContext.
public record MessageExhaustionSync(float exhaustion) implements CustomPacketPayload {

    public static final Type<MessageExhaustionSync> TYPE = // Changed: replaces integer channel ID
            new Type<>(Identifier.fromNamespaceAndPath(ClassicBar.MODID, "exhaustion_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageExhaustionSync> STREAM_CODEC = // Changed: replaces separate encode() and decode-constructor
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.exhaustion),
                    buf -> new MessageExhaustionSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Changed: replaces handle(Supplier<NetworkEvent.Context>); IPayloadContext.enqueueWork()
    // replaces ctx.get().enqueueWork(). NetworkHelper.getSidedPlayer() no longer needed;
    // Minecraft.getInstance().player used directly since this packet only goes to client.
    public static void handle(MessageExhaustionSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getFoodData().exhaustionLevel = msg.exhaustion; // Changed: setExhaustion(float) removed in MC 26.1; field exposed via access transformer
            }
        });
    }
}
