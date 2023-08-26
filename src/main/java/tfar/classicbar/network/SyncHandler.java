package tfar.classicbar.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import tfar.classicbar.compat.Helpers;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sync saturation (vanilla MC only syncs when it hits 0).
 * Sync exhaustion (vanilla MC does not sync it at all).
 */
public class SyncHandler {

  public SyncHandler() {}

  // Vanilla MC
  private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
  private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();

  // Tough as Nails
  private static final Map<UUID, Float> lastHydrationLevels = new HashMap<>();
  private static final Map<UUID, Float> lastThirstExhaustionLevels = new HashMap<>();

  @SubscribeEvent
  public void onLivingUpdateEvent(TickEvent.PlayerTickEvent event) {
    if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
      return;
    }

    syncVanillaData(player);

    if (Helpers.toughasnailsLoaded) {
      syncToughAsNailsData(player);
    }

  }

  private void syncVanillaData(ServerPlayer player) {
    UUID uuid = player.getUUID();
    Float lastSaturationLevel = lastSaturationLevels.get(uuid);
    Float lastExhaustionLevel = lastExhaustionLevels.get(uuid);

    float saturationLevel = player.getFoodData().getSaturationLevel();
    if (lastSaturationLevel == null || lastSaturationLevel != saturationLevel) {
      MessageSaturationSync msg = new MessageSaturationSync(saturationLevel);
      Message.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      lastSaturationLevels.put(uuid, saturationLevel);
    }

    float exhaustionLevel = player.getFoodData().getExhaustionLevel();
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      MessageExhaustionSync msg = new MessageExhaustionSync(exhaustionLevel);
      Message.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      lastExhaustionLevels.put(uuid, exhaustionLevel);
    }
  }

  /**
   * Whether the mod has been loaded should be ensured via the context.
   */
  private void syncToughAsNailsData(ServerPlayer player) {
    if (!ThirstHelper.isThirstEnabled()) return;

    UUID uuid = player.getUUID();
    Float lastHydrationLevel = lastHydrationLevels.get(uuid);
    Float lastExhaustionLevel = lastThirstExhaustionLevels.get(uuid);

    IThirst thirstData = ThirstHelper.getThirst(player);

    float hydrationLevel = thirstData.getHydration();
    if (lastHydrationLevel == null || lastHydrationLevel != hydrationLevel) {
      MessageHydrationSync msg = new MessageHydrationSync(hydrationLevel);
      Message.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      lastHydrationLevels.put(uuid, hydrationLevel);
    }

    float exhaustionLevel = thirstData.getExhaustion();
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      MessageThirstExhaustionSync msg = new MessageThirstExhaustionSync(exhaustionLevel);
      Message.INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      lastThirstExhaustionLevels.put(uuid, exhaustionLevel);
    }
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer)) return;
    UUID uuid = event.getEntity().getUUID();

    lastSaturationLevels.remove(uuid);
    lastExhaustionLevels.remove(uuid);
    if (Helpers.toughasnailsLoaded) {
      lastHydrationLevels.remove(uuid);
      lastThirstExhaustionLevels.remove(uuid);
    }
  }

}
