package tk.yongangame.mc.forge.forwardlib;


import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tk.yongangame.mc.data.PlayerData;

import java.util.UUID;

@Mod.EventBusSubscriber
public class ListenerService {
    private int id;

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event){
        id--;
        if (id == 0){
            ForwardPlayerData.getInstance().spigot = false;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        if (id == 0) {
            ForwardPlayerData.getInstance().playerData = new PlayerData(UUID.fromString(Minecraft.getInstance().getUser().getUuid()));
        }
        id++;
    }
}
