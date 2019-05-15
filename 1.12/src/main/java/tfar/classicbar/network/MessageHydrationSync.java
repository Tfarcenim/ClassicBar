package tfar.classicbar.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import toughasnails.thirst.ThirstHandler;

public class MessageHydrationSync implements IMessage, IMessageHandler<MessageHydrationSync, IMessage>
{
    private float hydrationLevel;

    public MessageHydrationSync(){}

    public MessageHydrationSync(float hydrationLevel)
    {
        this.hydrationLevel = hydrationLevel;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeFloat(hydrationLevel);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        hydrationLevel = buf.readFloat();
    }

    @Override
    public IMessage onMessage(final MessageHydrationSync message, final MessageContext ctx)
    {
        // defer to the next game loop; we can't guarantee that Minecraft.thePlayer is initialized yet
        EntityPlayer player = NetworkHelper.getSidedPlayer(ctx);
        ThirstHandler thirstHandler = ToughAsNailsHelper.getHandler(player);
        Minecraft.getMinecraft().addScheduledTask(() -> thirstHandler.setHydration(message.hydrationLevel));
        return null;
    }
}
