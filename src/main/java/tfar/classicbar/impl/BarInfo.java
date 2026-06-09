package tfar.classicbar.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
//this is for common values NOT meant to be touched by end user
public record BarInfo(String name, Set<String> dependencies, Predicate<Player> shouldRender,
                      BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator) {

    public BarInfo(String name,String dependency, Predicate<Player> shouldRender,
                   BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator) {
        this(name,Set.of(dependency),shouldRender,numerator,denominator);
    }

    public BarInfo(String name, Predicate<Player> shouldRender,
                   BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator) {
        this(name,Set.of(),shouldRender,numerator,denominator);
    }

    public boolean checkDependencies() {
        return dependencies.isEmpty() || dependencies.stream().allMatch(s -> ModList.get().isLoaded(s));
    }

    public float getRatio(Player player) {
        return Mth.clamp(numerator.getValue(player)/denominator.getValue(player),0,1);
    }
}
