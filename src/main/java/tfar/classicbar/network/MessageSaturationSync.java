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
//      registered via Message.channel.registerMessage() with an integer ID.
// New: record with TYPE (ResourceLocation identifier) + STREAM_CODEC replacing encode/decode.
public record MessageSaturationSync(float saturation) implements CustomPacketPayload {

    public static final Type<MessageSaturationSync> TYPE = // Changed: replaces integer channel ID
            new Type<>(Identifier.fromNamespaceAndPath(ClassicBar.MODID, "saturation_sync"));

    public static final StreamCodec<FriendlyByteBuf, MessageSaturationSync> STREAM_CODEC = // Changed: replaces separate encode() and decode-constructor
            StreamCodec.of(
                    (buf, msg) -> buf.writeFloat(msg.saturation),
                    buf -> new MessageSaturationSync(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Changed: replaces handle(Supplier<NetworkEvent.Context>); ctx.get().setPacketHandled(true)
    // and NetworkHelper.getSidedPlayer() are gone — IPayloadContext handles this implicitly.
    public static void handle(MessageSaturationSync msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getFoodData().setSaturation(msg.saturation);
            }
        });
    }
}
