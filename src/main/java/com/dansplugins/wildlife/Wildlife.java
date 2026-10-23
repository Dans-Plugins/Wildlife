package com.dansplugins.wildlife;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point for Wildlife. Features are scoped in MVP.md; so far only configuration is loaded.
 */
public final class Wildlife extends JavaPlugin {

    private WildlifeConfig wildlifeConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        wildlifeConfig = WildlifeConfig.from(getConfig());
        long enabled = wildlifeConfig.getAnimals().values().stream().filter(AnimalSettings::enabled).count();
        getLogger().info("Loaded " + enabled + " of " + wildlifeConfig.getAnimals().size() + " animals from config.yml.");
    }

    @Override
    public void onDisable() {
    }

    public WildlifeConfig getWildlifeConfig() {
        return wildlifeConfig;
    }
}
