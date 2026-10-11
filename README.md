# Weaponry (NeoForge 26.1.2)

Empty NeoForge skeleton for Minecraft **26.1.2** (NeoForge 26.1.2.68-beta, Java 25). It loads a `weaponry` mod with no
content yet; the weapons, abilities and effects will be ported here from the `fabric/26.1.2` branch.

## Where the build comes from

The Gradle setup (ModDevGradle, Gradle 9.4.1, Java toolchains through the foojay resolver) and the
`neoforge.mods.toml` template are `server-vein-miner`'s `neoforge/26.1.2` branch. Only `gradle.properties` is Weaponry's own; the mod metadata in
the toml is filled in from it at build time (`generateModMetadata`).

## Side

`neoforge_minecraft_side=BOTH`: Weaponry adds items, so it must be installed on the server and on every client.

## Commands

```
./gradlew build        # jar in build/libs
./gradlew runClient    # play-test
./gradlew runServer
```
