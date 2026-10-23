package com.dansplugins.wildlife;

import java.util.List;

/**
 * Configured settings for one animal in the roster, read from its section under {@code animals} in config.yml.
 *
 * @param id          the animal's key in config.yml, e.g. {@code deer}
 * @param enabled     whether the animal is active at all
 * @param spawnChance probability in [0.0, 1.0] used by natural spawning
 * @param biomes      lower-case biome keys the animal spawns in naturally, e.g. {@code forest}
 */
public record AnimalSettings(String id, boolean enabled, double spawnChance, List<String> biomes) {

    public AnimalSettings {
        biomes = List.copyOf(biomes);
    }

    /**
     * Returns true if the animal spawns naturally in the given biome. Matching ignores case and an optional
     * {@code minecraft:} namespace, so {@code FOREST} and {@code minecraft:forest} both match {@code forest}.
     */
    public boolean spawnsIn(String biome) {
        return biomes.contains(WildlifeConfig.normalizeKey(biome));
    }
}
