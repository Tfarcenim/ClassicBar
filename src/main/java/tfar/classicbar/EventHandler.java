package tfar.classicbar;

import com.google.gson.Gson;
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
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.overlays.mod.StaminaB;
import tfar.classicbar.impl.overlays.mod.Thirst;
import tfar.classicbar.util.ModUtils;

import java.io.*;
import java.util.*;

public class EventHandler implements IGuiOverlay {

  public static final Map<String, BarOverlay> registry = new HashMap<>();

  public void render(ForgeGui gui, GuiGraphics matrices, float partialTick, int screenWidth, int screenHeight) {

    Entity entity = ModUtils.mc.getCameraEntity();
    if (!(entity instanceof Player player)) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;
    ModUtils.mc.getProfiler().push("classicbars_hud");

    for (BarOverlay overlay : registry.values()) {
      BarSide rightHand = overlay.getSide();
      try {
        overlay.render(gui, matrices, player, screenWidth, screenHeight, getOffset(gui, rightHand));
      } catch (Error e) {
        ClassicBar.logger.error("disabling broken overlay "+overlay.name());
        e.printStackTrace();
        overlay.setErrored();
      }
    }
    //if (!errored.isEmpty()) all.removeAll(errored);

    ModUtils.mc.getProfiler().pop();
  }

  public static void increment(ForgeGui gui, BarSide side , int amount){
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
    ConfigCache.bake();
  }

  public static void sendModMessage(InterModEnqueueEvent e) {
    InterModComms.sendTo("vampirism", "disable-blood-bar", () -> true);
  }

  public static void setupOverlays(RegisterGuiOverlaysEvent e) {
    MinecraftForge.EVENT_BUS.addListener(EventHandler::disableOtherOverlays);
    e.registerBelow(VanillaGuiOverlay.ITEM_NAME.id(),ClassicBar.MODID,new EventHandler());

    //Register renderers for events
    ClassicBar.logger.info("Registering Overlays");

    //mod renderers
   // ClassicBar.logger.info("Registering Mod Overlays");
   // if (ModCompat.vampirism.loaded)EventHandler.register(new Blood());
  //  if (ModCompat.feathers.loaded)EventHandler.register(new Feathers());
   // if (ModCompat.parcool.loaded)EventHandler.register(new StaminaB());
    //if (ModCompat.toughasnails.loaded)EventHandler.register(new Thirst());
    // if (ModList.get().isLoaded("randomthings")) MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());
    // if (ModList.get().isLoaded("lavawaderbauble")) {
    //    MinecraftForge.EVENT_BUS.register(new LavaWaderBaubleRenderer());
    // }

    //if (ModList.get().isLoaded("superiorshields"))
    //  MinecraftForge.EVENT_BUS.register(new SuperiorShieldRenderer());

    //MinecraftForge.EVENT_BUS.register(new BetterDivingRenderer());
    //  if (ModList.get().isLoaded("botania")) MinecraftForge.EVENT_BUS.register(new TiaraBarRenderer());
  }

  public static void loadBarFiles() {
    Gson gson = new Gson();
    for (Map.Entry<String, Codec<? extends BarOverlay>> entry : BarRegistry.REGISTRY.entrySet()) {
      File file = FMLPaths.CONFIGDIR.get().resolve(ClassicBar.MODID).resolve(entry.getKey() + ".json").toFile();
      if (!file.exists()) {
        try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
          writer.setIndent("    ");
          gson.toJson(BarRegistry.DEFAULTS.get(entry.getKey()), writer);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        try (JsonReader reader = gson.newJsonReader(new FileReader(file))) {
          JsonObject json = gson.fromJson(reader, JsonObject.class);
          BarOverlay barOverlay = BarRegistry.REGISTRY.get(entry.getKey()).parse(new Dynamic<>(JsonOps.INSTANCE, json)).get().orThrow();
          registry.put(entry.getKey(), barOverlay);
        } catch (Exception e) {
          //write a new file
          try (JsonWriter writer = gson.newJsonWriter(new FileWriter(file))) {
            writer.setIndent("    ");
            gson.toJson(BarRegistry.DEFAULTS.get(entry.getKey()), writer);
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }

          try (JsonReader reader = gson.newJsonReader(new FileReader(file))) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            BarOverlay barOverlay = BarRegistry.REGISTRY.get(entry.getKey()).parse(new Dynamic<>(JsonOps.INSTANCE, json)).get().orThrow();
            registry.put(entry.getKey(), barOverlay);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }
      }
    }
  }

  private static final List<ResourceLocation> vanilla_overlays = List.of(VanillaGuiOverlay.AIR_LEVEL.id(),VanillaGuiOverlay.ARMOR_LEVEL.id(),
          VanillaGuiOverlay.PLAYER_HEALTH.id(),VanillaGuiOverlay.MOUNT_HEALTH.id(),VanillaGuiOverlay.FOOD_LEVEL.id());
  public static void disableOtherOverlays(RenderGuiOverlayEvent.Pre e) {
    NamedGuiOverlay overlay = e.getOverlay();
    if (vanilla_overlays.contains(overlay.id())) e.setCanceled(true);
    else if (overlay.id().getNamespace().equals("parcool") && StaminaB.checkConfigs()) e.setCanceled(true);
    else if (ModCompat.toughasnails.loaded && Thirst.isEnabled() && Thirst.OVERLAY_ID.equals(overlay.id())) e.setCanceled(true);
  }
}