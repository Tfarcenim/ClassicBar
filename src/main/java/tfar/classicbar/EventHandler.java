package tfar.classicbar;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraftforge.fml.ModList;
import tfar.classicbar.compat.Helpers;
import tfar.classicbar.config.ConfigCache;
import tfar.classicbar.api.BarOverlay;
import tfar.classicbar.overlays.mod.Blood;
import tfar.classicbar.overlays.mod.Feathers;
import tfar.classicbar.overlays.vanilla.*;

import java.util.*;

import static tfar.classicbar.util.ModUtils.mc;
import static tfar.classicbar.config.ClassicBarsConfig.leftorder;
import static tfar.classicbar.config.ClassicBarsConfig.rightorder;

public class EventHandler implements IGuiOverlay {

  private static final List<BarOverlay> all = new ArrayList<>();
  private static final Map<String, BarOverlay> registry = new HashMap<>();

  public static void register(BarOverlay iBarOverlay) {
    registry.put(iBarOverlay.name(), iBarOverlay);
  }

  public static void registerAll(BarOverlay... iBarOverlay) {
    Arrays.stream(iBarOverlay).forEach(overlay -> registry.put(overlay.name(), overlay));
  }

  public void render(ForgeGui gui, PoseStack matrices, float partialTick, int screenWidth, int screenHeight) {

    Entity entity = mc.getCameraEntity();
    if (!(entity instanceof Player player)) return;
    if (player.getAbilities().instabuild || player.isSpectator()) return;
    mc.getProfiler().push("classicbars_hud");

    int initial_rightHeight = gui.rightHeight;
    int initial_leftHeight = gui.leftHeight;

    for (BarOverlay overlay : all) {
      boolean rightHand = overlay.rightHandSide();
        overlay.render(gui, matrices, player, screenWidth, screenHeight, getOffset(gui,rightHand));
    }

   // mc.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
    mc.getProfiler().pop();
  }

  public static void increment(ForgeGui gui,boolean side ,int amount){
    if (side)gui.rightHeight+=amount;
    else gui.leftHeight+=amount;
  }

  public static int getOffset(ForgeGui gui,boolean right) {
    return right ? gui.rightHeight : gui.leftHeight;
  }

  public static void cacheConfigs() {
    all.clear();
    leftorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(false)));
    rightorder.get().stream().filter(s -> registry.get(s) != null).forEach(e -> all.add(registry.get(e).setSide(true)));
    ConfigCache.bake();
  }

  public static void setupOverlays(RegisterGuiOverlaysEvent e) {
    MinecraftForge.EVENT_BUS.addListener(EventHandler::disableVanilla);
    e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(),ClassicBar.MODID,new EventHandler());

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

  private static final List<ResourceLocation> overlays = List.of(VanillaGuiOverlay.AIR_LEVEL.id(),VanillaGuiOverlay.ARMOR_LEVEL.id(),
          VanillaGuiOverlay.PLAYER_HEALTH.id(),VanillaGuiOverlay.MOUNT_HEALTH.id(),VanillaGuiOverlay.FOOD_LEVEL.id());
  public static void disableVanilla(RenderGuiOverlayEvent.Pre e) {
    NamedGuiOverlay overlay = e.getOverlay();
    if (overlays.contains(overlay.id())) e.setCanceled(true);
  }
}