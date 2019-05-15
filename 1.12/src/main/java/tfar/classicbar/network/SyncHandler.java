package tfar.classicbar.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tfar.classicbar.ClassicBar;
import tfar.classicbar.ModUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SyncHandler {
  public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(ClassicBar.MODID);

  public static void init() {
    CHANNEL.registerMessage(MessageExhaustionSync.class, MessageExhaustionSync.class, 1, Side.CLIENT);
    CHANNEL.registerMessage(MessageSaturationSync.class, MessageSaturationSync.class, 2, Side.CLIENT);
    if (ClassicBar.TOUGHASNAILS) {
      CHANNEL.registerMessage(MessageHydrationSync.class, MessageHydrationSync.class, 3, Side.CLIENT);
      CHANNEL.registerMessage(MessageThirstExhaustionSync.class, MessageThirstExhaustionSync.class, 4, Side.CLIENT);
    }

    MinecraftForge.EVENT_BUS.register(new SyncHandler());
  }

  /*
   * Sync saturation (vanilla MC only syncs when it hits 0)
   * Sync exhaustion (vanilla MC does not sync it at all)
   */
  private static final Map<UUID, Float> lastSaturationLevels = new HashMap<>();
  private static final Map<UUID, Float> lastExhaustionLevels = new HashMap<>();
  private static final Map<UUID, Float> lastHydrationLevels = new HashMap<>();
  private static final Map<UUID, Float> lastThirstExhaustionLevels = new HashMap<>();


  @SubscribeEvent
  public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
    if (!(event.getEntity() instanceof EntityPlayerMP))
      return;

    EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
    Float lastSaturationLevel = lastSaturationLevels.get(player.getUniqueID());
    Float lastHydrationLevel = lastHydrationLevels.get(player.getUniqueID());
    Float lastExhaustionLevel = lastExhaustionLevels.get(player.getUniqueID());
    Float lastThirstExhaustionLevel = lastThirstExhaustionLevels.get(player.getUniqueID());


    if (lastSaturationLevel == null || lastSaturationLevel != player.getFoodStats().getSaturationLevel()) {
      CHANNEL.sendTo(new MessageSaturationSync(player.getFoodStats().getSaturationLevel()), player);
      lastSaturationLevels.put(player.getUniqueID(), player.getFoodStats().getSaturationLevel());
    }

    float exhaustionLevel = ModUtils.getExhaustion(player);
    if (lastExhaustionLevel == null || Math.abs(lastExhaustionLevel - exhaustionLevel) >= 0.01f) {
      CHANNEL.sendTo(new MessageExhaustionSync(exhaustionLevel), player);
      lastExhaustionLevels.put(player.getUniqueID(), exhaustionLevel);
    }

    if (ClassicBar.TOUGHASNAILS) {
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
    }
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (!(event.player instanceof EntityPlayerMP))
      return;

    lastSaturationLevels.remove(event.player.getUniqueID());
    lastExhaustionLevels.remove(event.player.getUniqueID());
    if (ClassicBar.TOUGHASNAILS) {
      lastHydrationLevels.remove(event.player.getUniqueID());
      lastThirstExhaustionLevels.remove(event.player.getUniqueID());
    }
  }
}
