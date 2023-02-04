package tk.yongangame.mc.forge.forwardlib;

import com.google.common.base.Predicates;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tk.yongangame.mc.data.PlayerData;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;

public class ForwardPlayerData {
    private static ForwardPlayerData instance;

    private ForwardPlayerData() {
    }

    public static ForwardPlayerData getInstance() {
        if (instance == null) {
            instance = new ForwardPlayerData();
        }
        return instance;
    }

    private SimpleChannel channel;
    private static final int IDX = 1;
    private final Gson gson = new Gson();
    public PlayerData playerData;


    public boolean spigot = false;

    public void clientSetup(FMLClientSetupEvent event) {
        channel = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation("forward_lib","player_data"))
                .networkProtocolVersion(() -> "114514")
                .serverAcceptedVersions(Predicates.not(NetworkRegistry.ACCEPTVANILLA::equals))
                .clientAcceptedVersions(Predicates.not(NetworkRegistry.ACCEPTVANILLA::equals))
                .simpleChannel();
        channel.registerMessage(IDX, PlayerData.class, this::Enc, this::Dec, this::Proc);
    }

    private void Enc(Object str, ByteBuf buffer) {
        buffer.writeBytes(gson.toJson(str).getBytes(StandardCharsets.UTF_8));
    }

    private PlayerData Dec(ByteBuf buffer) {
        return gson.fromJson(buffer.toString(StandardCharsets.UTF_8),PlayerData.class);
    }

    public void Proc(PlayerData str, Supplier<NetworkEvent.Context> supplier) {
        if (!spigot){
            spigot=true;
        }

        if (Objects.equals(str.uuid, playerData.uuid)){
            playerData = str;
        }
        NetworkEvent.Context context = supplier.get();
        context.setPacketHandled(true);
    }

    public void Send(PlayerData msg) {
        channel.sendToServer(msg);
    }
}
