package tfar.classicbar;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.classicbar.config.ModConfig;
import tfar.classicbar.overlays.BarOverlay;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static tfar.classicbar.ModUtils.mc;
import static tfar.classicbar.config.ModConfig.leftorder;
import static tfar.classicbar.config.ModConfig.rightorder;

public class EventHandler {

  private static final List<BarOverlay> combined = new ArrayList<>();
  private static final Map<String, BarOverlay> registry = new HashMap<>();

  public static void register(BarOverlay iBarOverlay) {
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(BarOverlay... iBarOverlay) {
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
    if (!(entity instanceof PlayerEntity)) return;
    PlayerEntity player = (PlayerEntity) entity;
    if (player.abilities.isCreativeMode || player.isSpectator()) return;
    mc.getProfiler().startSection("classicbars_hud");

    int scaledWidth = mc.getMainWindow().getScaledWidth();
    int scaledHeight = mc.getMainWindow().getScaledHeight();

    int initial_right_height = ForgeIngameGui.right_height;
    int initial_left_height = ForgeIngameGui.left_height;

    mc.getTextureManager().bindTexture(ModUtils.ICON_BAR);
    Supplier<Stream<BarOverlay>> supplier = () -> combined.stream().filter(iBarOverlay -> iBarOverlay.shouldRender(player));

    MatrixStack matrices = event.getMatrixStack();

    supplier.get().forEach(iBarOverlay -> {
      iBarOverlay.renderBar(matrices,player, scaledWidth, scaledHeight);
        increment(iBarOverlay.rightHandSide(),10);
    });

    ForgeIngameGui.right_height = initial_right_height;
    ForgeIngameGui.left_height = initial_left_height;

    supplier.get().forEach(iBarOverlay -> {
      if (iBarOverlay.shouldRenderText())
        iBarOverlay.renderText(matrices,player, scaledWidth, scaledHeight);
        increment(iBarOverlay.rightHandSide(),10);
    });

    if (ModConfig.displayIcons.get()) {
      ForgeIngameGui.right_height = initial_right_height;
      ForgeIngameGui.left_height = initial_left_height;

      supplier.get().forEach(iBarOverlay -> {
        iBarOverlay.renderIcon(matrices,player, scaledWidth, scaledHeight);
          increment(iBarOverlay.rightHandSide(),10);
      });
    }
    mc.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
    mc.getProfiler().endSection();
  }

  public void increment(boolean side ,int amount){
    if (side)ForgeIngameGui.right_height+=amount;
    else ForgeIngameGui.left_height+=amount;
  }

  public static void setup() {
    combined.clear();
    leftorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> combined.add(registry.get(e).setSide(false)));
    rightorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> combined.add(registry.get(e).setSide(true)));
  }
}