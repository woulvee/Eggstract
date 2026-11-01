# Eggstract!

A Minecraft mod for NeoForge 26.1.2 that adds an enchantment for swords and spears called "Eggstracting", which allows you to obtain Spawn Eggs in Survival Mode.

Enchanted books with Eggstracting have their own textures without any other mod. It also supports CIT in Optifine format, tested with [CIT Resewn (NeoForge)](https://www.curseforge.com/minecraft/mc-mods/cit-resewn-neoforge), and [Enchantment Descriptions](https://modrinth.com/mod/enchantment-descriptions). With [JEI](https://modrinth.com/mod/jei) installed, it shows which mobs drop their egg and how to get the enchantment. With [Just Enough Resources](https://modrinth.com/mod/just-enough-resources-jer) installed, the eggs also appear on its mob pages.

## Configuration

The settings apply to all worlds. They are in `config/eggstract-common.toml`, or in-game under Mods > Eggstract! > Config. All chances are percentages from 0 to 100.

On a dedicated server, the file on the server decides the gameplay. JEI and JER show the values of the client's own file, so they can differ if the two files do.

### Spawn egg drops

| Level | Default drop chance |
| --- | --- |
| I | 0.5% |
| II | 1% |
| III | 1.5% |
| Above III | 100% |

`blacklist` lists the mobs that never drop their egg. Entries can be a mob (`minecraft:wither`), all mobs of a mod (`somemod:*`) or an entity tag (`#minecraft:raiders`).

### Sources

| Source | Default | Chance applies |
| --- | --- | --- |
| Enchanting table | on, 2% | per offer, only offers with a high level cost qualify |
| Trading | off, 1% | per enchanted book trade of a villager |
| Bartering | off, 0.5% | per piglin barter |
| Fishing | on, 0.2% | per catch |
| Generated loot | on, 1% | per loot chest |

The enchantment is deliberately in no vanilla enchantment tag, so it only appears through these sources.

## Development

JEI and Just Enough Resources are included in the development client. Run `gradlew runClient` with Java 25.
