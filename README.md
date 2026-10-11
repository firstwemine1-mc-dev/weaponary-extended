# Weaponry (Forge 1.16.5)

Empty Forge skeleton for Minecraft **1.16.5** (Forge 36.2.42, Java 8, official Mojang mappings). It loads a
`weaponry` mod with no content yet; the weapons, abilities and effects will be ported here from the
`fabric/1.16.5` branch.

## Where the build comes from

The Gradle setup is `server-vein-miner`'s `forge/1.20.1` branch (ForgeGradle 6.0.16, Gradle 8.6, Java
toolchains through the foojay resolver), with only the Minecraft, Forge and Java versions changed. The
Forge versions are the latest on maven.minecraftforge.net for each Minecraft version, except 1.20.1, which
keeps the 47.3.0 that `server-vein-miner` already builds with. Lothrazar's TemplateMod
(`../../forge-template-mod`) was checked as a reference; its 1.16 branch uses MCP snapshot mappings and an old
Gradle, so it isn't used directly.

## Side

`displayTest = MATCH_VERSION` and the Minecraft dependency is `BOTH`: Weaponry adds items, so it must be
installed on the server and on every client.

## Commands

Gradle 8.6 can't run on Java 22 or newer, so start it on JDK 21 (the mod itself is compiled for its own Java
version through the toolchain). In PowerShell: `$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"`.

```
./gradlew build        # jar in build/libs (reobfuscated)
./gradlew runClient    # play-test
./gradlew runServer
```
