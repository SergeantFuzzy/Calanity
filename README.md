# Calanity

Calanity is a modular MMORPG framework for Paper 1.21.x servers that mixes clans, combat-focused classes, and GUI-driven management. The project is split into two Maven modules:

- **calanity-api** – lightweight public API shared with addons.
- **calanity-plugin** – the Paper implementation that ships with built-in classes, clans, listeners, commands, GUIs, and hooks.

## Highlights
- Clan power, ranks, and leaderboards with configurable buffs.
- Class system with active, passive, and clan abilities bound to hotbar slots.
- YAML/SQLite/MySQL data stores switched via configuration.
- Addon SPI so external jars in `/plugins/Calanity/addons` can register new content.
- PlaceholderAPI, WorldGuard, and Vault integration points.
- HUD/scoreboard renderer, GUI menus, holograms, and debug tooling.

## Modules
| Module | Description |
| ------ | ----------- |
| `calanity-api` | Contracts for clans, classes, stats, events, placeholders, and addon loading. | 
| `calanity-plugin` | Paper `JavaPlugin` that implements managers, services, abilities, commands, and integrations. |

## Development Notes
- Requires Java 21+
- Target PaperMC 1.21.8–1.21.x
- Configs live under `/plugins/Calanity/`
- Addons implement `CalanityAddon` and are discovered automatically at startup.
