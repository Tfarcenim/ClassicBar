package tfar.classicbar.compat;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.world.entity.player.Player;

public class VampirismHelper {

    public static boolean isVampire(Player player) {
        return VampirismAPI.factionPlayerHandler(player).isInFaction(VReference.VAMPIRE_FACTION);
    }
}
