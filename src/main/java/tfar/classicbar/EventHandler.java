package tfar.classicbar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.api.BarRegistry;
import tfar.classicbar.api.BarSide;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.impl.overlays.mod.ParcoolStaminaB;
import tfar.classicbar.impl.overlays.mod.ToughAsNailsThirst;

import java.io.*;
import java.util.*;

public class EventHandler implements IGuiOverlay {

  private static final List<BarOverlay> registry = new ArrayList<>();

  public void render(ForgeGui gui, GuiGraphics matrices, float partialTick, int screenWidth, int screenHeight) {

    Entity entity = gui.getMinecraft().getCameraEntity();
    if (!(entity instanceof Player player)) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;
    gui.getMinecraft().getProfiler().push("classicbars_hud");

    for (BarOverlay overlay : registry) {
      BarSide side = overlay.getSide();
      try {
        if (overlay.render(gui, matrices, player, screenWidth, screenHeight, getOffset(gui, side))) {
          increment(gui, side, 10);
        }
      } catch (Error e) {
          ClassicBar.logger.error("disabling broken overlay {}", overlay.name());
        e.printStackTrace();
        overlay.setErrored();
      }
    }
    gui.getMinecraft().getProfiler().pop();
  }

  public static void increment(ForgeGui gui, BarSide side, int amount){
    switch (side) {
      case LEFT ->gui.leftHeight+=amount;
      case RIGHT ->gui.rightHeight+=amount;
    }
  }

  public static int getOffset(ForgeGui gui, BarSide side) {
    return switch (side) {
      case RIGHT -> gui.rightHeight;
      case LEFT -> gui.leftHeight;
    };
  }

  public static void cacheConfigs() {
    BarRegistry.init();
    loadBarFiles();
    registry.sort(Comparator.comparingInt(o -> ClassicBarsConfig.priority.get().indexOf(o.name())));
  }

  public static void sendModMessage(InterModEnqueueEvent e) {
    InterModComms.sendTo("vampirism", "disable-blood-bar", () -> true);
  }

  public static void setupOverlays(RegisterGuiOverlaysEvent e) {
    MinecraftForge.EVENT_BUS.addListener(EventHandler::disableOtherOverlays);
    e.registerBelow(VanillaGuiOverlay.ITEM_NAME.id(),ClassicBar.MODID,new EventHandler());
  }

  public static void loadBarFiles() {
    registry.clear();
    overlaysToDisable.clear();
    FMLPaths.CONFIGDIR.get().resolve(ClassicBar.MODID).toFile().mkdirs();
    Gson gson = new Gson();
    for (Map.Entry<String, BarOverlay> entry : BarRegistry.REGISTRY.entrySet()) {
      File file = FMLPaths.CONFIGDIR.get().resolve(ClassicBar.MODID).resolve(entry.getKey() + ".json").toFile();
      if (!file.exists()) {
        try {
          tryWrite(gson,entry.getKey(),file);
          tryRead(gson,entry.getKey(),file);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        try {
          tryRead(gson,entry.getKey(),file);
        } catch (Exception e) {
          //write a new file
          try  {
            tryWrite(gson,entry.getKey(),file);
            tryRead(gson,entry.getKey(),file);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }
      }
    }
  }

  static void tryWrite(Gson gson,String name,File file)throws IOException {
    try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
      writer.setIndent("    ");

      BarOverlay overlay = BarRegistry.REGISTRY.get(name);
      Codec<BarOverlay> codec = (Codec<BarOverlay>) overlay.codec();

      JsonElement element = codec.encodeStart(JsonOps.INSTANCE,overlay).resultOrPartial(ClassicBar.logger::error).orElseThrow();
      gson.toJson(element, writer);
    }
  }

    static void tryRead(Gson gson,String name,File file)throws IOException {
    try (JsonReader reader = gson.newJsonReader(new FileReader(file))) {
      JsonObject json = gson.fromJson(reader, JsonObject.class);
      BarOverlay barOverlay = BarRegistry.REGISTRY.get(name).codec().parse(new Dynamic<>(JsonOps.INSTANCE, json)).get().orThrow();
      registry.add(barOverlay);
      barOverlay.disablesOverlay().ifPresent(overlaysToDisable::add);
    }
  }

  private static final Set<ResourceLocation> overlaysToDisable = new HashSet<>();

  public static void disableOtherOverlays(RenderGuiOverlayEvent.Pre e) {
    NamedGuiOverlay overlay = e.getOverlay();

    if (overlaysToDisable.contains(overlay.id())) {
      e.setCanceled(true);
    }
  }
}