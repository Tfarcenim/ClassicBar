package tfar.classicbar.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tfar.classicbar.compat.ToughAsNailsHelper;
import toughasnails.thirst.ThirstHandler;

public class MessageThirstExhaustionSync implements IMessage, IMessageHandler<MessageThirstExhaustionSync, IMessage>
{
    private float thirstExhaustionLevel;

    public MessageThirstExhaustionSync(){}

    public MessageThirstExhaustionSync(float thirstExhaustionLevel)
    {
        this.thirstExhaustionLevel = thirstExhaustionLevel;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(thirstExhaustionLevel);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        thirstExhaustionLevel = buf.readFloat();
    }

    @Override
    public IMessage onMessage(final MessageThirstExhaustionSync message, final MessageContext ctx)
    {
        // defer to the next game loop; we can't guarantee that Minecraft.thePlayer is initialized yet
        EntityPlayer player = NetworkHelper.getSidedPlayer(ctx);
        ThirstHandler thirstHandler = ToughAsNailsHelper.getHandler(player);
        Minecraft.getMinecraft().addScheduledTask(() -> thirstHandler.setExhaustion(message.thirstExhaustionLevel));
        return null;
    }
}
