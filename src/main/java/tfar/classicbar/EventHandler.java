package tfar.classicbar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.fml.ModList;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.overlays.mod.Blood;
import tfar.classicbar.overlays.mod.Feathers;
import tfar.classicbar.overlays.vanilla.*;

import java.util.*;

import static tfar.classicbar.config.ClassicBarsConfig.leftorder;
import static tfar.classicbar.config.ClassicBarsConfig.rightorder;
import static tfar.classicbar.util.ModUtils.mc;

public class EventHandler implements IIngameOverlay {

  private static final List<BarOverlay> all = new ArrayList<>();
  private static final Map<String, BarOverlay> registry = new HashMap<>();

  public static void register(BarOverlay iBarOverlay) {
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(BarOverlay... iBarOverlay) {
    Arrays.stream(iBarOverlay).forEach(overlay -> registry.put(overlay.name(), overlay));
  }

  public void render(ForgeIngameGui gui, PoseStack matrices, float partialTick, int screenWidth, int screenHeight) {
    Entity entity = mc.getCameraEntity();
    if (!(entity instanceof Player player)) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;

    mc.getProfiler().push("classicbars_hud");

    int initial_right_height = gui.right_height;
    int initial_left_height = gui.left_height;

    for (BarOverlay overlay : all) {
      boolean rightHand = overlay.rightHandSide();
      overlay.render(gui, matrices, player, screenWidth, screenHeight, getOffset(gui,rightHand));
    }

   // mc.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
    mc.getProfiler().pop();
  }

  public static void increment(ForgeIngameGui gui,boolean side ,int amount){
    if (side)gui.right_height+=amount;
    else gui.left_height+=amount;
  }

  public static int getOffset(ForgeIngameGui gui,boolean right) {
    return right ? gui.right_height : gui.left_height;
  }

  public static void cacheConfigs() {
    all.clear();
    leftorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(false)));
    rightorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(true)));
    ConfigCache.bake();
  }

  public static void setupOverlays() {
    EventHandler.disableVanilla();
    OverlayRegistry.registerOverlayBelow(ForgeIngameGui.CHAT_PANEL_ELEMENT,ClassicBar.MODID,new EventHandler());

    //Register renderers for events
    ClassicBar.logger.info("Registering Vanilla Overlays");

    EventHandler.registerAll(new Absorption(), new Air(), new Armor(), new ArmorToughness(),
            new Health(), new Hunger(), new MountHealth());
    if (Helpers.vampirismloaded)EventHandler.register(new Blood());
    if (Helpers.elenaiDodgeLoaded)EventHandler.register(new Feathers());

    //mod renderers
    ClassicBar.logger.info("Registering Mod Overlays");
    // if (ModList.get().isLoaded("randomthings")) MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());
    if (ModList.get().isLoaded("lavawaderbauble")) {
      //  MinecraftForge.EVENT_BUS.register(new LavaWaderBaubleRenderer());
    }

    //if (ModList.get().isLoaded("superiorshields"))
    //  MinecraftForge.EVENT_BUS.register(new SuperiorShieldRenderer());

    //MinecraftForge.EVENT_BUS.register(new BetterDivingRenderer());
    //  if (ModList.get().isLoaded("botania")) MinecraftForge.EVENT_BUS.register(new TiaraBarRenderer());

  }

  public static void disableVanilla() {
    OverlayRegistry.enableOverlay(ForgeIngameGui.AIR_LEVEL_ELEMENT,false);
    OverlayRegistry.enableOverlay(ForgeIngameGui.ARMOR_LEVEL_ELEMENT,false);
    OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT,false);
    OverlayRegistry.enableOverlay(ForgeIngameGui.MOUNT_HEALTH_ELEMENT,false);
    OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT,false);
  }
}