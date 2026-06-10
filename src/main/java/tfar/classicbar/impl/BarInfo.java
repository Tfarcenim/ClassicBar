package tfar.classicbar.impl;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.util.*;
import java.util.function.Predicate;
//this is for common values NOT meant to be touched by end user
public record BarInfo(String name, Set<String> dependencies, Predicate<Player> shouldRender,
                      List<BarOverlayImpl.Numerator> numerators, BarOverlayImpl.Denominator denominator, Object2IntFunction<Player> activeLayers) {

    public static BarInfo createSimpleVanilla(String name, Predicate<Player> shouldRender,
                                              BarOverlayImpl.Numerator numerator, BarOverlayImpl.Denominator denominator) {
        return new BarInfo(name,Set.of(),shouldRender,List.of(numerator),denominator,p -> 1);
    }

    public boolean checkDependencies() {
        return dependencies.isEmpty() || dependencies.stream().allMatch(s -> ModList.get().isLoaded(s));
    }

    public float getRatio(Player player, int layer) {
        return Mth.clamp(getUnclampedRatio(player,layer),0,1);
    }

    public float getUnclampedRatio(Player player,int layer) {
        return numerators.get(layer).getValue(player)/denominator.getValue(player);
    }

    public static Builder getBuilder(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private final Set<String> dependencies = new HashSet<>();
        private Predicate<Player> shouldRender = s -> true;
        private List<BarOverlayImpl.Numerator> numerators = new ArrayList<>();
        private BarOverlayImpl.Denominator denominator = p -> 20;
        private Object2IntFunction<Player> activeLayers = p -> 1;

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

        public Builder addNumerator(BarOverlayImpl.Numerator numerator) {
            numerators.add(numerator);
            return this;
        }

        public Builder setDenominator(BarOverlayImpl.Denominator denominator) {
            Objects.requireNonNull(denominator);
            this.denominator = denominator;
            return this;
        }

        public Builder setActiveLayers(Object2IntFunction<Player> activeLayers) {
            this.activeLayers = activeLayers;
            return this;
        }

        public BarInfo build() {
            if (numerators.isEmpty()) {
                throw new IllegalStateException("numerators is empty");
            }
            return new BarInfo(name,dependencies,shouldRender,numerators,denominator,activeLayers);
        }
    }
}
