package tfar.classicbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import java.util.*;

import static tfar.classicbar.ModUtils.ICON_VANILLA;
import static tfar.classicbar.ModUtils.mc;

public class EventHandler {

  private static final List<IBarOverlay> left = new ArrayList<>();
  private static final List<IBarOverlay> right = new ArrayList<>();
  private static final Map<String,IBarOverlay> registry = new HashMap<>();
  public static final Minecraft mc = Minecraft.getMinecraft();

  public static void register(IBarOverlay iBarOverlay){
    registry.put(iBarOverlay.name(),iBarOverlay);
  }

  public static void registerAll(IBarOverlay... iBarOverlay){
    Arrays.stream(iBarOverlay).forEach( overlay -> registry.put(overlay.name(),overlay));
  }

  @SubscribeEvent
  public void renderBars(RenderGameOverlayEvent.Pre event) {
    //cancel all events that the mod handles as we don't want them to draw
    switch (event.getType()) {
      case AIR:
      case ARMOR:
      case HEALTHMOUNT:
      case FOOD:
      case HEALTH:
        event.setCanceled(true);
        default:return;
      case ALL:
    }
    Entity entity = mc.getRenderViewEntity();
    if (!(entity instanceof EntityPlayer)) return;
    EntityPlayer player = (EntityPlayer) entity;
    if (player.capabilities.isCreativeMode)return;
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();

    left.stream()
            .filter(iBarOverlay -> iBarOverlay.shouldRender(player))
            .forEach(iBarOverlay -> {
              iBarOverlay.render(player, scaledWidth, scaledHeight);
              GuiIngameForge.left_height += 10;
            });
    right.stream()
            .filter(iBarOverlay -> iBarOverlay.shouldRender(player))
            .forEach(iBarOverlay -> {
              iBarOverlay.render(player, scaledWidth, scaledHeight);
              GuiIngameForge.right_height += 10;
            });
  }

  public static void setup(){
    left.clear();
    right.clear();
    Arrays.stream(ModConfig.general.overlays.leftorder).forEach(e -> {
      if (registry.get(e) != null)
      left.add(registry.get(e));
    });
    Arrays.stream(ModConfig.general.overlays.rightorder).forEach(e -> {
      if (registry.get(e) != null)
        right.add(registry.get(e));
    });
  }
}