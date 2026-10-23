package com.dansplugins.wildlife;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WildlifeConfigTest {

    private static WildlifeConfig parse(String yaml) throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(yaml);
        return WildlifeConfig.from(config);
    }

    @Test
    void defaultConfigDefinesTheV1Roster() throws IOException, InvalidConfigurationException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(in, "config.yml must be on the classpath");
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            WildlifeConfig parsed = WildlifeConfig.from(config);

            assertEquals(List.of("deer", "boar", "duck", "crow"), List.copyOf(parsed.getAnimals().keySet()));
            for (AnimalSettings animal : parsed.getAnimals().values()) {
                assertTrue(animal.enabled(), animal.id());
                assertFalse(animal.biomes().isEmpty(), animal.id());
            }
            assertTrue(parsed.getDisabledWorlds().isEmpty());
        }
    }

    @Test
    void readsAnimalSettings() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  deer:
                    enabled: false
                    spawn-chance: 0.25
                    biomes: [forest, taiga]
                """);

        AnimalSettings deer = config.getAnimal("deer").orElseThrow();
        assertFalse(deer.enabled());
        assertEquals(0.25, deer.spawnChance());
        assertEquals(List.of("forest", "taiga"), deer.biomes());
    }

    @Test
    void missingKeysFallBackToDefaults() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  duck: {}
                """);

        AnimalSettings duck = config.getAnimal("duck").orElseThrow();
        assertEquals(WildlifeConfig.DEFAULT_ENABLED, duck.enabled());
        assertEquals(WildlifeConfig.DEFAULT_SPAWN_CHANCE, duck.spawnChance());
        assertTrue(duck.biomes().isEmpty());
    }

    @Test
    void spawnChanceIsClampedToUnitInterval() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  boar:
                    spawn-chance: 3.5
                  crow:
                    spawn-chance: -1
                """);

        assertEquals(1.0, config.getAnimal("boar").orElseThrow().spawnChance());
        assertEquals(0.0, config.getAnimal("crow").orElseThrow().spawnChance());
    }

    @Test
    void nonNumericSpawnChanceFallsBackToDefault() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  deer:
                    spawn-chance: .nan
                  boar:
                    spawn-chance: often
                """);

        assertEquals(WildlifeConfig.DEFAULT_SPAWN_CHANCE, config.getAnimal("deer").orElseThrow().spawnChance());
        assertEquals(WildlifeConfig.DEFAULT_SPAWN_CHANCE, config.getAnimal("boar").orElseThrow().spawnChance());
    }

    @Test
    void biomeAndAnimalKeysAreNormalized() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  Duck:
                    biomes: [RIVER, "minecraft:swamp", river, " "]
                """);

        AnimalSettings duck = config.getAnimal("DUCK").orElseThrow();
        assertEquals("duck", duck.id());
        assertEquals(List.of("river", "swamp"), duck.biomes());
        assertTrue(duck.spawnsIn("minecraft:RIVER"));
        assertTrue(duck.spawnsIn("SWAMP"));
        assertFalse(duck.spawnsIn("desert"));
    }

    @Test
    void nonSectionAnimalEntriesAreIgnored() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                animals:
                  deer: true
                  boar:
                    enabled: true
                """);

        assertEquals(Set.of("boar"), config.getAnimals().keySet());
    }

    @Test
    void isActiveRespectsAnimalFlagAndDisabledWorlds() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                disabled-worlds: [world_nether]
                animals:
                  deer:
                    enabled: true
                  boar:
                    enabled: false
                """);

        assertTrue(config.isActive("deer", "world"));
        assertFalse(config.isActive("deer", "world_nether"));
        assertFalse(config.isActive("boar", "world"));
        assertFalse(config.isActive("crow", "world"), "an animal with no section is not active");
    }

    @Test
    void disabledWorldNamesAreCaseSensitive() throws InvalidConfigurationException {
        WildlifeConfig config = parse("""
                disabled-worlds: [World]
                """);

        assertTrue(config.isWorldDisabled("World"));
        assertFalse(config.isWorldDisabled("world"));
    }

    @Test
    void emptyConfigHasNoAnimals() throws InvalidConfigurationException {
        WildlifeConfig config = parse("");

        assertTrue(config.getAnimals().isEmpty());
        assertTrue(config.getDisabledWorlds().isEmpty());
    }
}
