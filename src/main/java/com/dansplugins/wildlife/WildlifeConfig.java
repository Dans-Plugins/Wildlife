package com.dansplugins.wildlife;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Plugin configuration parsed from config.yml. Plain Java apart from the {@link ConfigurationSection} it reads,
 * so it can be tested without a running server.
 */
public final class WildlifeConfig {

    static final boolean DEFAULT_ENABLED = true;
    static final double DEFAULT_SPAWN_CHANCE = 0.05;

    private final Map<String, AnimalSettings> animals;
    private final Set<String> disabledWorlds;

    private WildlifeConfig(Map<String, AnimalSettings> animals, Set<String> disabledWorlds) {
        this.animals = Collections.unmodifiableMap(animals);
        this.disabledWorlds = Collections.unmodifiableSet(disabledWorlds);
    }

    /**
     * Parses the configuration. Missing keys fall back to their defaults, and a spawn chance outside
     * [0.0, 1.0] is clamped into that range.
     */
    public static WildlifeConfig from(ConfigurationSection config) {
        Map<String, AnimalSettings> animals = new LinkedHashMap<>();
        ConfigurationSection animalsSection = config.getConfigurationSection("animals");
        if (animalsSection != null) {
            for (String key : animalsSection.getKeys(false)) {
                ConfigurationSection section = animalsSection.getConfigurationSection(key);
                if (section == null) {
                    continue;
                }
                String id = normalizeKey(key);
                animals.put(id, parseAnimal(id, section));
            }
        }
        // World names are case-sensitive in Bukkit, so they are kept as written.
        Set<String> disabledWorlds = new LinkedHashSet<>(config.getStringList("disabled-worlds"));
        return new WildlifeConfig(animals, disabledWorlds);
    }

    private static AnimalSettings parseAnimal(String id, ConfigurationSection section) {
        boolean enabled = section.getBoolean("enabled", DEFAULT_ENABLED);
        double spawnChance = Math.max(0.0, Math.min(1.0, section.getDouble("spawn-chance", DEFAULT_SPAWN_CHANCE)));
        List<String> biomes = new ArrayList<>();
        for (String biome : section.getStringList("biomes")) {
            String key = normalizeKey(biome);
            if (!key.isEmpty() && !biomes.contains(key)) {
                biomes.add(key);
            }
        }
        return new AnimalSettings(id, enabled, spawnChance, biomes);
    }

    /** Lower-cases a key and strips a leading {@code minecraft:} namespace. */
    static String normalizeKey(String key) {
        String normalized = key.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith("minecraft:") ? normalized.substring("minecraft:".length()) : normalized;
    }

    /** Every configured animal, keyed by lower-case id, in config order. */
    public Map<String, AnimalSettings> getAnimals() {
        return animals;
    }

    public Optional<AnimalSettings> getAnimal(String id) {
        return Optional.ofNullable(animals.get(normalizeKey(id)));
    }

    public Set<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    /** Returns true if the whole roster is turned off in the named world. */
    public boolean isWorldDisabled(String worldName) {
        return disabledWorlds.contains(worldName);
    }

    /**
     * Returns true if the animal is configured, enabled, and the world is not disabled.
     * An animal with no section in config.yml is not active.
     */
    public boolean isActive(String animalId, String worldName) {
        return !isWorldDisabled(worldName) && getAnimal(animalId).map(AnimalSettings::enabled).orElse(false);
    }
}
