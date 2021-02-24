package tfar.classicbar.compat;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;

public class Helpers {

	public static final boolean vampirismloaded = ModList.get().isLoaded("vampirism");
	public static final boolean elenaiDodgeLoaded = ModList.get().isLoaded("elenaidodge2");


	@CapabilityInject(IFactionPlayerHandler.class)
	private static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;

	public static boolean isVampire(PlayerEntity entity) {
		return getFactionPlayerHandler(entity).map(h ->
						VReference.VAMPIRE_FACTION.equals(h.getCurrentFaction())).orElse(false);
	}

	public static LazyOptional<IFactionPlayerHandler> getFactionPlayerHandler(PlayerEntity player) {
		return player.getCapability(CAP_FACTION_HANDLER_PLAYER, (Direction)null);
	}

}
