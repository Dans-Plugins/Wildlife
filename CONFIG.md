# Configuration

Wildlife writes `plugins/Wildlife/config.yml` on first start. Changes take effect on the next server restart.

> **Status:** the configuration is read and validated, but natural spawning, drops and the spawn command are not implemented yet (see [MVP.md](MVP.md)). Until they are, these settings have no in-game effect.

## Top-level keys

| Key | Type | Default | Description |
|---|---|---|---|
| `disabled-worlds` | list of world names | `[]` | Worlds in which the whole roster is turned off. World names are case-sensitive and must match the folder name exactly. |
| `animals` | section | see below | One sub-section per animal, keyed by the animal's id. An animal with no section is not active. |

## Per-animal keys (`animals.<id>`)

| Key | Type | Default if missing | Description |
|---|---|---|---|
| `enabled` | boolean | `true` | Whether the animal is active at all. |
| `spawn-chance` | number, 0.0–1.0 | `0.05` | Probability used by natural spawning. Values outside 0.0–1.0 are clamped into that range. |
| `biomes` | list of biome keys | `[]` | Biomes the animal spawns in naturally, e.g. `forest`. Matching ignores case and an optional `minecraft:` prefix; duplicates and blank entries are dropped. Use biome keys that exist on your server version. |

Animal ids are matched case-insensitively and stored in lower case.

## Shipped defaults

| Animal | `spawn-chance` | `biomes` |
|---|---|---|
| `deer` | `0.05` | `forest`, `birch_forest`, `taiga`, `meadow` |
| `boar` | `0.05` | `forest`, `dark_forest`, `taiga` |
| `duck` | `0.05` | `river`, `swamp`, `mangrove_swamp` |
| `crow` | `0.05` | `plains`, `forest`, `dark_forest` |

All four are `enabled: true` by default, and `disabled-worlds` is empty.
