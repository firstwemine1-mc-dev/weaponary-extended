# Weaponry (NeoForge 1.21.4)

Empty NeoForge skeleton for Minecraft **1.21.4** (NeoForge 21.4.158, Java 21). It loads a `weaponry` mod with no
content yet; the weapons, abilities and effects will be ported here from the `fabric/1.21.4` branch.

## Where the build comes from

The Gradle setup (ModDevGradle, Gradle 9.4.1, Java toolchains through the foojay resolver) and the
`neoforge.mods.toml` template are `server-vein-miner`'s `neoforge/1.21.1` branch (there is no 1.21.4 NeoForge branch there), with the NeoForge version set to 21.4.158, the latest for 1.21.4 on maven.neoforged.net. Only `gradle.properties` is Weaponry's own; the mod metadata in
the toml is filled in from it at build time (`generateModMetadata`).

## Side

`neoforge_minecraft_side=BOTH`: Weaponry adds items, so it must be installed on the server and on every client.

## Commands

```
./gradlew build        # jar in build/libs
./gradlew runClient    # play-test
./gradlew runServer
```
