package tfar.classicbar.compat;

import net.minecraft.world.entity.player.Player;

// Changed (MC 26.1 upgrade): Vampirism compat is temporarily disabled because no 26.1 build
// of Vampirism is available, so the de.teamlapen.vampirism.* API is off the classpath.
// isVampire() now always returns false; restore the
// VampirismAPI.factionPlayerHandler(player).isInFaction(VReference.VAMPIRE_FACTION) lookup
// once a 26.1-compatible Vampirism API is available.
public final class VampirismHelper {
    private VampirismHelper() {} // §16: utility class — private no-arg constructor

    public static boolean isVampire(Player player) {
        return false;
    }
}
