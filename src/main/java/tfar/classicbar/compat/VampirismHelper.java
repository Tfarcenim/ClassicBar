package tfar.classicbar.compat;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.world.entity.player.Player;

// Changed: removed Forge Capability<IFactionPlayerHandler> + CAP_FACTION_HANDLER_PLAYER field,
// getFactionPlayerHandler(Player) method, and LazyOptional-based lookup.
// Old: player.getCapability(CAP_FACTION_HANDLER_PLAYER).map(h -> VAMPIRE_FACTION.equals(h.getCurrentFaction()))
// New: VampirismAPI.factionPlayerHandler(player).isInFaction(VReference.VAMPIRE_FACTION)
// The Vampirism 1.21 API exposes factionPlayerHandler() directly, so the capability
// indirection is no longer needed. getFactionPlayerHandler() was removed; if external
// code needs the handler it should call VampirismAPI directly.
public final class VampirismHelper {
    private VampirismHelper() {} // §16: utility class — private no-arg constructor

    // Changed: was LazyOptional<IFactionPlayerHandler>-based; now delegates to VampirismAPI directly
    public static boolean isVampire(Player player) {
        return VampirismAPI.factionPlayerHandler(player).isInFaction(VReference.VAMPIRE_FACTION);
    }
}
