package tfar.classicbar;

import javax.annotation.Nonnull;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.compat.ModCompat;
import tfar.classicbar.config.ClassicBarsConfig;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.impl.overlays.mod.Blood;
import tfar.classicbar.impl.overlays.mod.HomeostaticWater;
import tfar.classicbar.impl.overlays.mod.IronsMana;
import tfar.classicbar.impl.overlays.mod.StaminaB;
import tfar.classicbar.impl.overlays.mod.Thirst;
import tfar.classicbar.impl.overlays.mod.ThirstWasTaken;
import tfar.classicbar.impl.overlays.vanilla.*;
import tfar.classicbar.util.ModUtils;

import java.util.*;

public class EventHandler implements LayeredDraw.Layer {

  private static Gui forgeGui() {
    return Minecraft.getInstance().gui;
  }

  private static final List<BarOverlay> all = new ArrayList<>();
  // Changed: was private; made public so ClassicBarsConfig.readBarSettings() can look up
  // overlays by name when applying per-bar JSON settings.
  public static final Map<String, BarOverlay> registry = new HashMap<>();
  private static final List<BarOverlay> errored = new ArrayList<>();

  public static void register(BarOverlay iBarOverlay) {
    if (iBarOverlay == null) return; // Defensive null guard — matches the null check in registerAll()
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(BarOverlay... iBarOverlay) {
    Arrays.stream(iBarOverlay).forEach(overlay -> {
      if (overlay != null) {
        registry.put(overlay.name(), overlay);
      }
    });
  }

  @Override
  public void render(@Nonnull GuiGraphics matrices, @Nonnull DeltaTracker deltaTracker) {
    int screenWidth = matrices.guiWidth();
    int screenHeight = matrices.guiHeight();
    Gui gui = forgeGui();

    Entity entity = ModUtils.mc.getCameraEntity();
    if (!(entity instanceof Player player)) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;
    ModUtils.mc.getProfiler().push("classicbars_hud");

    // Changed: removed the pre-loop "if (errored.contains(overlay)) continue;" check.
    // Instead, broken overlays are batch-removed after the loop with removeAll(), which
    // is more efficient and avoids checking the errored list on every iteration.
    for (BarOverlay overlay : all) {
      boolean rightHand = overlay.rightHandSide();
      try {
        overlay.render(matrices, player, screenWidth, screenHeight, getOffset(gui, rightHand));
      } catch (Error e) {
        ClassicBar.logger().error("Removing broken overlay {}", overlay.name(), e); // NeoForge 1.21: use Logger instead of printStackTrace()
        errored.add(overlay);
      }
    }
    if (!errored.isEmpty()) {
      all.removeAll(errored); // Changed: moved removal out of loop; batch-clears all newly errored overlays at once
      errored.clear(); // Fix: clear after batch removal to prevent stale entries accumulating across frames
    }

    ModUtils.mc.getProfiler().pop();
  }

  public static void increment(Gui gui, boolean side, int amount) {
    if (side) gui.rightHeight += amount;
    else gui.leftHeight += amount;
  }

  public static int getOffset(Gui gui, boolean right) {
    return right ? gui.rightHeight : gui.leftHeight;
  }

  public static void cacheConfigs() {
    all.clear();
    ClassicBarsConfig.leftorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(false)));
    ClassicBarsConfig.rightorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(true)));
    all.removeAll(errored);
    ConfigCache.bake();
  }

  public static void sendModMessage(InterModEnqueueEvent e) {
    InterModComms.sendTo("vampirism", "disable-blood-bar", () -> true);
  }

  public static void setupOverlays(RegisterGuiLayersEvent e) {
    NeoForge.EVENT_BUS.addListener(EventHandler::disableOtherOverlays);
    e.registerBelow(VanillaGuiLayers.SELECTED_ITEM_NAME,
            ResourceLocation.fromNamespaceAndPath(ClassicBar.MODID, "hud"),
            new EventHandler());

    ClassicBar.logger().info("Registering Vanilla Overlays");
    EventHandler.registerAll(new Absorption(), new Air(), new Armor(), new ArmorToughness(),
            new Health(), new Hunger(), new MountHealth());

    ClassicBar.logger().info("Registering Mod Overlays");
    if (ModCompat.vampirism.loaded) EventHandler.register(new Blood());
    if (ModCompat.parcool.loaded) EventHandler.register(new StaminaB());
    if (ModCompat.toughasnails.loaded) EventHandler.register(new Thirst());
    if (ModCompat.thirstWasTaken.loaded) EventHandler.register(new ThirstWasTaken());
    if (ModCompat.homeostatic.loaded) EventHandler.register(new HomeostaticWater());
    if (ModCompat.ironsSpellbooks.loaded) EventHandler.register(new IronsMana());

    cacheConfigs();
    ClassicBarsConfig.readBarSettings();
  }

  private static final List<ResourceLocation> vanilla_overlays = List.of(
          VanillaGuiLayers.AIR_LEVEL,
          VanillaGuiLayers.ARMOR_LEVEL,
          VanillaGuiLayers.PLAYER_HEALTH,
          VanillaGuiLayers.VEHICLE_HEALTH,
          VanillaGuiLayers.FOOD_LEVEL);

  private static final ResourceLocation PARCOOL_STAMINA_HUD = ResourceLocation.fromNamespaceAndPath("parcool", "hud.stamina");
  // Changed: ResourceLocation for the Overloaded Armor Bar mod's GUI layer; cancelled when ClassicBar is handling armor rendering
  private static final ResourceLocation OVERLOADED_ARMOR_BAR_HUD = ResourceLocation.fromNamespaceAndPath("overloadedarmorbar", "overloadedarmorbar");
  // Changed: ResourceLocation for Iron's Spells n Spellbooks mana overlay; cancelled when ClassicBar renders mana as a horizontal bar
  private static final ResourceLocation IRONS_MANA_OVERLAY = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "mana_overlay");

  public static void disableOtherOverlays(RenderGuiLayerEvent.Pre e) {
    ResourceLocation id = e.getName();
    if (vanilla_overlays.contains(id)) e.setCanceled(true);
    else if (ModCompat.toughasnails.loaded && Thirst.isEnabled() && Thirst.OVERLAY_ID.equals(id)) e.setCanceled(true);
    else if (ModCompat.parcool.loaded && PARCOOL_STAMINA_HUD.equals(id)) e.setCanceled(true);
    else if (ModCompat.overloadedArmorBar.loaded && OVERLOADED_ARMOR_BAR_HUD.equals(id)) e.setCanceled(true); // Suppress Overloaded Armor Bar — ClassicBar already renders layered armor colors
    else if (ModCompat.ironsSpellbooks.loaded && IRONS_MANA_OVERLAY.equals(id)) e.setCanceled(true); // Suppress Iron's Spells mana bar — ClassicBar renders mana as a horizontal bar
  }
}
