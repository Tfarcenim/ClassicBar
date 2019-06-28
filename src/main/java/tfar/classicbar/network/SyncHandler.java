package tfar.classicbar.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import tfar.classicbar.ModUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler {

  public SyncHandler() {}

  /*
   * Sync saturation (vanilla MC only syncs when it hits 0)
   * Sync exhaustion (vanilla MC does not sync it at all)
   */
  private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
  private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();
  //private static final Map<UUID, Float> lastHydrationLevels = new HashMap<>();
  //private static final Map<UUID, Float> lastThirstExhaustionLevels = new HashMap<>();


  @SubscribeEvent
  public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
    if (!(event.getEntity() instanceof ServerPlayerEntity))
      return;

    ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
    Float lastSaturationLevel = lastSaturationLevels.get(player.getUniqueID());
    //Float lastHydrationLevel = lastHydrationLevels.get(player.getUniqueID());
    Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUniqueID());
    //Float lastThirstExhaustionLevel = lastThirstExhaustionLevels.get(player.getUniqueID());


    if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodStats().getSaturationLevel()) {

      Object msg = new MessageSaturationSync(player.getFoodStats().getSaturationLevel());
      Message.INSTANCE.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
      lastSaturationLevels.put(player.getUniqueID(), player.getFoodStats().getSaturationLevel());
    }

    float exhaustionLevel = ModUtils.getExhaustion(player);
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      Object msg = new MessageExhaustionSync(ModUtils.getExhaustion(player));
      Message.INSTANCE.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
      lastExhaustionLevels.put(player.getUniqueID(), exhaustionLevel);
    }

    /*if (ClassicBar.TOUGHASNAILS) {
      if (lastHydrationLevel == null ||
              lastHydrationLevel != ToughAsNailsHelper.getHandler(player).getHydration()) {
        CHANNEL.sendTo(new MessageHydrationSync(ToughAsNailsHelper.getHandler(player).getHydration()), player);
        lastHydrationLevels.put(player.getUniqueID(), ToughAsNailsHelper.getHandler(player).getHydration());
      }

      if (lastThirstExhaustionLevel == null ||
              lastThirstExhaustionLevel != ToughAsNailsHelper.getHandler(player).getExhaustion()) {
        CHANNEL.sendTo(new MessageThirstExhaustionSync(ToughAsNailsHelper.getHandler(player).getExhaustion()), player);
        lastThirstExhaustionLevels.put(player.getUniqueID(), ToughAsNailsHelper.getHandler(player).getExhaustion());
      }
    }*/
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.getPlayer() instanceof ServerPlayerEntity))
      return;

    lastSaturationLevels.remove(event.getPlayer().getUniqueID());
    lastExhaustionLevels.remove(event.getPlayer().getUniqueID());
    /*if (ClassicBar.TOUGHASNAILS) {
      lastHydrationLevels.remove(event.player.getUniqueID());
      lastThirstExhaustionLevels.remove(event.player.getUniqueID());
    }*/
  }
}
