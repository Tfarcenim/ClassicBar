package tfar.classicbar.compat;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class VampirismHelper {
    private static final Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isVampire(Player entity) {
        return getFactionPlayerHandler(entity).map(h ->
                VReference.VAMPIRE_FACTION.equals(h.getCurrentFaction())).orElse(false);
    }

    public static LazyOptional<IFactionPlayerHandler> getFactionPlayerHandler(Player player) {
        return player.getCapability(CAP_FACTION_HANDLER_PLAYER, null);
    }
}
