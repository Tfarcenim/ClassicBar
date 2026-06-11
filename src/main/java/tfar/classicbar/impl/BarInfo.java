package tfar.classicbar.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.util.*;
import java.util.function.Predicate;
//this is for common values NOT meant to be touched by end user
public record BarInfo(String name, Set<String> dependencies, Predicate<Player> shouldRender,
                      BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator,
                      IconData icon_data) {

    public static BarInfo createSimpleVanilla(String name, Predicate<Player> shouldRender,
                                              BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator) {
        return new BarInfo(name,Set.of(),shouldRender,numerator,denominator,IconData.DEFAULT_DATA);
    }

    public boolean checkDependencies() {
        return dependencies.isEmpty() || dependencies.stream().allMatch(s -> ModList.get().isLoaded(s));
    }

    public float getRatio(Player player) {
        return Mth.clamp(getUnclampedRatio(player),0,1);
    }

    public float getUnclampedRatio(Player player) {
        return numerator.getValue(player)/denominator.getValue(player);
    }

    public static Builder getBuilder(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private final Set<String> dependencies = new HashSet<>();
        private Predicate<Player> shouldRender = s -> true;
        private BarOverlayImpl.Numerator numerator;
        private BarOverlayImpl.Denominator denominator = p -> 20;
        private IconData iconData = IconData.DEFAULT_DATA;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setShouldRender(Predicate<Player> shouldRender) {
            Objects.requireNonNull(shouldRender);
            this.shouldRender = shouldRender;
            return this;
        }

        public Builder requireDependency(String... dependencies) {
            this.dependencies.addAll(List.of(dependencies));
            return this;
        }

        public Builder setNumerator(BarOverlayImpl.Numerator numerator) {
            this.numerator = numerator;
            return this;
        }

        public Builder setDenominator(BarOverlayImpl.Denominator denominator) {
            Objects.requireNonNull(denominator);
            this.denominator = denominator;
            return this;
        }

        public Builder setIconData(IconData iconData) {
            this.iconData = iconData;
            return this;
        }

        public BarInfo build() {
            if (numerator == null) {
                throw new IllegalStateException("numerator is empty");
            }
            return new BarInfo(name,dependencies,shouldRender, numerator,denominator, iconData);
        }
    }
}
