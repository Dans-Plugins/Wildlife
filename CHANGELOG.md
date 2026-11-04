# Changelog

All notable changes to this project are documented in this file. The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added

- `config.yml` with per-animal `enabled`, `spawn-chance` and `biomes` settings for deer, boar, duck and crow, and a `disabled-worlds` list that turns the whole roster off per world. The settings are loaded on startup but have no in-game effect yet. See [CONFIG.md](CONFIG.md).

### Fixed

- `disabled-worlds` and `biomes` given as a single value without list brackets, such as `disabled-worlds: world_nether`, are now read as a one-entry list instead of being silently ignored.
