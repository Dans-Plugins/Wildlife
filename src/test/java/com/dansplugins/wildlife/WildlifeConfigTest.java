package com.dansplugins.wildlife;

import org.bukkit.block.Biome;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    void defaultConfigBiomesExistInBukkitApi() throws IOException, InvalidConfigurationException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(in, "config.yml must be on the classpath");
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            WildlifeConfig parsed = WildlifeConfig.from(config);

            // getField only looks up the constant; it does not initialise Biome, which needs a running server
            // on versions where Biome is registry-backed.
            for (AnimalSettings animal : parsed.getAnimals().values()) {
                for (String biome : animal.biomes()) {
                    String constant = biome.toUpperCase(Locale.ROOT);
                    assertDoesNotThrow(() -> Biome.class.getField(constant),
                            animal.id() + " lists a biome the Bukkit API does not define: " + biome);
                }
            }
        }
    }

    @Test
    void configMdShippedDefaultsMatchDefaultConfig()throws IOException, InvalidConfigurationException {
        WildlifeConfig parsed;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(in, "config.yml must be on the classpath");
            YamlConfiguration config = new YamlConfiguration();
            config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            parsed = WildlifeConfig.from(config);
        }

        List<List<String>> rows = configMdTable("## Shipped defaults");
        List<String> documentedIds = new ArrayList<>();
        for (List<String> row : rows) {
            String id = row.get(0);
            documentedIds.add(id);
            AnimalSettings animal = parsed.getAnimal(id).orElseThrow(() -> new AssertionError("not in config.yml: " + id));
            assertEquals(Double.parseDouble(row.get(1)), animal.spawnChance(), id);
            assertEquals(List.of(row.get(2).split(",\\s*")), animal.biomes(), id);
        }
        assertEquals(List.copyOf(parsed.getAnimals().keySet()), documentedIds);
    }

    @Test
    void configMdPerAnimalDefaultsMatchCode() throws IOException {
        Map<String, String> defaults = new HashMap<>();
        for (List<String> row : configMdTable("## Per-animal keys")) {
            defaults.put(row.get(0), row.get(2));
        }

        assertEquals(Set.of("enabled", "spawn-chance", "biomes"), defaults.keySet());
        assertEquals(WildlifeConfig.DEFAULT_ENABLED, Boolean.parseBoolean(defaults.get("enabled")));
        assertEquals(WildlifeConfig.DEFAULT_SPAWN_CHANCE, Double.parseDouble(defaults.get("spawn-chance")));
        assertEquals("[]", defaults.get("biomes"));
    }

    /**
     * Returns the body rows of the first Markdown table under the given CONFIG.md heading, with each cell
     * trimmed and stripped of backticks.
     */
    private static List<List<String>> configMdTable(String heading) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("CONFIG.md"), StandardCharsets.UTF_8);
        int start = 0;
        while (start < lines.size() && !lines.get(start).startsWith(heading)) {
            start++;
        }
        assertTrue(start < lines.size(), "CONFIG.md has no heading " + heading);
        List<List<String>> rows = new ArrayList<>();
        boolean inTable = false;
        for (String line : lines.subList(start + 1, lines.size())) {
            if (line.startsWith("|")) {
                inTable = true;
                List<String> cells = new ArrayList<>();
                for (String cell : line.substring(1, line.lastIndexOf('|')).split("\\|")) {
                    cells.add(cell.replace("`", "").trim());
                }
                rows.add(cells);
            } else if (inTable || line.startsWith("#")) {
                break;
            }
        }
        assertTrue(rows.size() > 2, "CONFIG.md table under " + heading + " has no body rows");
        // Drop the header row and the |---| separator.
        return rows.subList(2, rows.size());
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
