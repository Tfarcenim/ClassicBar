package tfar.classicbar;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.IBarOverlay;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static tfar.classicbar.ModUtils.mc;
import static tfar.classicbar.config.ModConfig.general;

public class EventHandler {

  private static final List<IBarOverlay> combined = new ArrayList<>();
  private static final Map<String, IBarOverlay> registry = new HashMap<>();

  public static void register(IBarOverlay iBarOverlay) {
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(IBarOverlay... iBarOverlay) {
    Arrays.stream(iBarOverlay).forEach(overlay -> registry.put(overlay.name(), overlay));
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
      default:
        return;
      case ALL:
    }
    Entity entity = mc.getRenderViewEntity();
    if (!(entity instanceof EntityPlayer)) return;
    EntityPlayer player = (EntityPlayer) entity;
    if (player.capabilities.isCreativeMode) return;
    int scaledWidth = event.getResolution().getScaledWidth();
    int scaledHeight = event.getResolution().getScaledHeight();

    int initial_right_height = GuiIngameForge.right_height;
    int initial_left_height = GuiIngameForge.left_height;

    mc.getTextureManager().bindTexture(ModUtils.ICON_BAR);
    Supplier<Stream<IBarOverlay>> supplier = () -> combined.stream().filter(iBarOverlay -> iBarOverlay.shouldRender(player));

    supplier.get().forEach(iBarOverlay -> {
      iBarOverlay.renderBar(player, scaledWidth, scaledHeight);
      if (iBarOverlay.rightHandSide())
       GuiIngameForge.right_height+=10;
      else GuiIngameForge.left_height+=10;
    });

    GuiIngameForge.right_height = initial_right_height;
    GuiIngameForge.left_height = initial_left_height;

    supplier.get().forEach(iBarOverlay -> {
      if (iBarOverlay.shouldRenderText())
      iBarOverlay.renderText(player, scaledWidth, scaledHeight);
      if (iBarOverlay.rightHandSide())
        GuiIngameForge.right_height+=10;
      else GuiIngameForge.left_height+=10;
    });

    if (general.displayIcons) {
      GuiIngameForge.right_height = initial_right_height;
      GuiIngameForge.left_height = initial_left_height;

      supplier.get().forEach(iBarOverlay -> {
        iBarOverlay.renderIcon(player, scaledWidth, scaledHeight);
        if (iBarOverlay.rightHandSide())
          GuiIngameForge.right_height += 10;
        else GuiIngameForge.left_height += 10;
      });
    }
    mc.getTextureManager().bindTexture(Gui.ICONS);
  }

  public static void setup() {
    combined.clear();
    Arrays.stream(ModConfig.general.overlays.leftorder).forEach(e -> {
      if (registry.get(e) != null) {

        combined.add(

                registry.get(e).setSide(false));
      }
    });
    Arrays.stream(ModConfig.general.overlays.rightorder).forEach(e -> {
      if (registry.get(e) != null)
        combined.add(registry.get(e).setSide(true));
    });
  }
}