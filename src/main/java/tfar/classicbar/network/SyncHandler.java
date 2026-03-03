package tfar.classicbar.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tfar.classicbar.compat.ModCompat;
import toughasnails.api.thirst.IThirst;
import toughasnails.api.thirst.ThirstHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sync saturation (vanilla MC only syncs when it hits 0).
 * Sync exhaustion (vanilla MC does not sync it at all).
 * Also sync counterparts of thirst data since copied from the vanilla hunger system.
 */
public final class SyncHandler {

  private static SyncHandler INSTANCE;

  public static SyncHandler instance() {
    if (INSTANCE == null) {
      INSTANCE = new SyncHandler();
    }
    return INSTANCE;
  }

  private SyncHandler() {}

  // Vanilla MC
  private final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
  private final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();

  // Tough as Nails
  private final Map<UUID, Float> lastHydrationLevels = new HashMap<>();
  private final Map<UUID, Float> lastThirstExhaustionLevels = new HashMap<>();

  // Changed: was TickEvent.PlayerTickEvent with phase == Phase.END guard.
  // PlayerTickEvent.Post fires only at end-of-tick so no phase check is needed.
  @SubscribeEvent
  public void onPlayerTick(PlayerTickEvent.Post event) {
    if (!(event.getEntity() instanceof ServerPlayer player)) return; // Changed: getEntity() replaces event.player

    syncVanillaData(player);

    if (ModCompat.toughasnails.loaded) {
      syncToughAsNailsData(player);
    }
  }

  private void syncVanillaData(ServerPlayer player) {
    UUID uuid = player.getUUID();
    Float lastSaturationLevel = lastSaturationLevels.get(uuid);
    Float lastExhaustionLevel = lastExhaustionLevels.get(uuid);

    float saturationLevel = player.getFoodData().getSaturationLevel();
    if (lastSaturationLevel == null || lastSaturationLevel != saturationLevel) {
      PacketDistributor.sendToPlayer(player, new MessageSaturationSync(saturationLevel)); // Changed: was Message.channel().sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT)
      lastSaturationLevels.put(uuid, saturationLevel);
    }

    float exhaustionLevel = player.getFoodData().getExhaustionLevel();
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      PacketDistributor.sendToPlayer(player, new MessageExhaustionSync(exhaustionLevel)); // Changed: was Message.channel().sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT)
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
      PacketDistributor.sendToPlayer(player, new MessageHydrationSync(hydrationLevel)); // Changed: was Message.channel().sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT)
      lastHydrationLevels.put(uuid, hydrationLevel);
    }

    float exhaustionLevel = thirstData.getExhaustion();
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      PacketDistributor.sendToPlayer(player, new MessageThirstExhaustionSync(exhaustionLevel)); // Changed: was Message.channel().sendTo(msg, connection, NetworkDirection.PLAY_TO_CLIENT)
      lastThirstExhaustionLevels.put(uuid, exhaustionLevel);
    }
  }

  @OnlyIn(Dist.CLIENT)
  @SubscribeEvent
  public void onClientPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
    Message.presentOnServer = true; // Changed: was Message.channel().isRemotePresent(conn); NeoForge payload system guarantees the server has the mod if the connection was established, so always true
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    if (!(event.getEntity() instanceof ServerPlayer)) return;
    UUID uuid = event.getEntity().getUUID();

    lastSaturationLevels.remove(uuid);
    lastExhaustionLevels.remove(uuid);
    if (ModCompat.toughasnails.loaded) {
      lastHydrationLevels.remove(uuid);
      lastThirstExhaustionLevels.remove(uuid);
    }
  }
}
