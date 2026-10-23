package com.dansplugins.wildlife;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimalSettingsTest {

    @Test
    void biomeListIsDefensivelyCopied() {
        List<String> biomes = new ArrayList<>(List.of("forest"));
        AnimalSettings deer = new AnimalSettings("deer", true, 0.1, biomes);

        biomes.add("taiga");

        assertEquals(List.of("forest"), deer.biomes());
        assertThrows(UnsupportedOperationException.class, () -> deer.biomes().add("desert"));
    }

    @Test
    void spawnsInMatchesNormalizedBiomeKeys() {
        AnimalSettings deer = new AnimalSettings("deer", true, 0.1, List.of("birch_forest"));

        assertTrue(deer.spawnsIn("birch_forest"));
        assertTrue(deer.spawnsIn("BIRCH_FOREST"));
        assertTrue(deer.spawnsIn("minecraft:birch_forest"));
        assertFalse(deer.spawnsIn("forest"));
    }
}
