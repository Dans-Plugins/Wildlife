# Wildlife

## Description

Wildlife is a Minecraft plugin that populates a survival world with more animals than vanilla provides — deer, boar, ducks and crows — without asking a single player to install anything.

That last part is the whole design. A server cannot give a vanilla client a new kind of creature; the client only knows how to draw the mobs it shipped with. Wildlife works within that: each animal is a vanilla mob given a new name, new attributes, new drops and new spawning rules. A deer is a real thing you hunt in the forest. It is also, underneath, a mob Minecraft already knows.

This plugin was proposed from a Kingdom: First Era player request on 2026-09-04.

## Project Status

**Pre-implementation.** The repository holds a buildable plugin skeleton with no features yet. The scope of the first release is defined in [MVP.md](MVP.md) — and the honest limits of the approach are documented there. Read them before promising anyone a shark.

## Planned Features

- A roster of new animals built from reskinned vanilla mobs
- Biome-appropriate natural spawning
- Distinct drops that feed cooking and trade
- Optional resource-pack support for real models, for servers that ship a pack

## Installation

Not yet released. Once there is a release, installation will follow the usual DPC pattern: drop the jar in `plugins/` and restart. It will also be available through [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager).

### Dependencies

None. Coexists with [Wild Pets](https://github.com/Dans-Plugins/Wild-Pets), which handles taming.

## Usage

### Documentation

- [MVP](MVP.md) – Scope of the first release, and its deliberate non-goals

`USER_GUIDE.md`, `COMMANDS.md`, `CONFIG.md` and `CHANGELOG.md` land with the first release, per [DOCUMENTATION_PRACTICES.md](https://github.com/Dans-Plugins/dpc-conventions/blob/main/docs/DOCUMENTATION_PRACTICES.md).

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Wildlife/issues/new).

## Contributing

This repository follows the standards in [dpc-conventions](https://github.com/Dans-Plugins/dpc-conventions). `CONTRIBUTING.md` is not written yet; until it is, build and test with `./gradlew clean build` on JDK 21 (the jar is written to `build/libs/`).

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

See the [LICENSE](LICENSE) file for full details.
