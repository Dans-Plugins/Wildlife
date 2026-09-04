# Wildlife — MVP

## Problem

Players asked for ducks, deer, grizzly bears, sharks, crows and boar. The underlying want is that a vanilla overworld feels empty and domesticated — cows, sheep, pigs and chickens, all of them livestock. A forest with nothing wild in it is not a wilderness.

## Target environment

Spigot, Minecraft 26.1, **vanilla clients**.

**State this plainly to anyone who asks for this plugin:** a Spigot server cannot add a new entity type. The client draws only the models it shipped with. Every "new animal" here is a vanilla mob with a different name, different stats, different drops and different spawn rules — a deer that looks like a goat, a boar that looks like a hog. That is either an acceptable trade or it is not, and it is better to say so before the work than after it.

The escape hatch is a resource pack, which *can* retexture a mob variant into a genuinely different-looking creature. That is out of scope for v1 but the plugin should be built so a pack can be added without rewriting it.

## The smallest shippable slice

### v1 does

1. **A small roster.** Four animals, chosen because a plausible vanilla base exists for each:
   - **Deer** — from a goat or horse base; flees on sight, drops venison and hide.
   - **Boar** — from a hoglin or pig base; retaliates when struck.
   - **Duck** — from a chicken base; found near water.
   - **Crow** — from a bat or parrot base; ambient, scavenging.
2. **Natural spawning by biome**, at configurable rates, so these are found rather than summoned.
3. **Distinct drops** that are their own items, so hunting feeds cooking and trade rather than duplicating beef.
4. **Configuration** to enable, disable and re-tune each animal individually — including turning the whole roster off per world.
5. **A spawn command** for operators and for events.

### v1 does not

- **Ship sharks or grizzly bears.** Both were asked for; both are the weakest fits. There is no convincing vanilla aquatic predator base short of reusing a drowned or a dolphin, and a bear from a polar bear base is only a bear in cold biomes. Both are better once a resource pack exists.
- **Ship a resource pack, or require one.**
- **Add custom AI.** v1 selects and tunes vanilla behaviour; it does not write pathfinding.
- **Add taming or pets.** [Wild Pets](https://github.com/Dans-Plugins/Wild-Pets) already does that, and is already installed on the servers asking for this.
- **Add breeding for the new animals.**
- **Replace or suppress vanilla mob spawning.**
- **Touch mob griefing, spawn caps or performance tuning** — those are server settings, and "fix mobs" (also on the original request) is a separate and much vaguer ask that needs clarifying before anyone builds for it.

## Why these non-goals

This is the highest effort-to-payoff item on the whole player wishlist, and the one most likely to disappoint regardless of how well it is built, because the gap between "a deer" and "a goat named Deer" is exactly the gap a resource pack fills. Four animals that spawn correctly and drop something worth having is a real improvement to a wilderness. Twelve animals that are all obviously reskins is a running joke on the server.

## Open questions

- Which vanilla base for each animal? This is the single decision that determines whether the plugin feels good, and it should be prototyped in-world and *looked at* before any of it is written properly.
- Should drops be [Conquest-Recipes](https://github.com/Dans-Plugins/Conquest-Recipes)-style named items, so venison and hide slot into the same item system as everything else medieval on the server?
- Is a resource pack actually on the table? If the answer is yes, the roster and the base choices change completely — a pack lets a mob variant become a genuinely new-looking creature, and sharks and bears come back onto the list.
- Performance: additional natural spawns on a single-node server with an existing mob cap needs measuring, not assuming.

## Dependencies

None.
