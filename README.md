# Weaponry

Fabric mod for Minecraft **1.21.1** (Java 21, Mojang mappings). It adds 10 melee weapons in
5 material tiers, for 50 items in total. This first pass covers base stats only; abilities come later.

Built from the official [fabric-example-mod](https://github.com/FabricMC/fabric-example-mod)
template (`1.21.1` branch). A pristine copy of that template lives at `../fabric-example-mod`.

## Side

`"environment": "*"`. Install the mod on **both client and server**. New items are registered on the server and
synced to the client, so the mod can't be client-only, and a vanilla client can't join a server that has it.

## Weapons

| Weapon | Dmg vs sword | Speed | Reach | Durability × | Two-handed* |
|---|---:|---:|---:|---:|---|
| Dagger | −2 | 2.4 | 2.5 | 0.75 | |
| Sabre | −1 | 2.0 | 3.0 | 1.0 | |
| Scimitar | 0 | 1.6 | 3.0 | 1.0 | |
| Spear | −1 | 1.4 | 4.0 | 1.0 | |
| Pike | 0 | 1.0 | 5.0 | 1.2 | yes |
| Lance | −2 | 0.8 | 4.5 | 0.6 | yes |
| Halberd | +2 | 0.9 | 4.0 | 1.2 | yes |
| Hatchet | +1 | 1.1 | 2.75 | 1.0 | |
| War Hammer | +3 | 0.7 | 3.0 | 1.5 | yes |
| Club | −1 | 1.2 | 3.0 | 1.5 | |

Tiers are wooden, stone, iron, golden and diamond. Damage bonus, enchantability, repair item and base
durability all come from the vanilla tier. Source: `../pixel-art/weaponry/docs/weapon-stats.md`.

\* Recorded in `WeaponType`, but not enforced yet.

## Recipes (M = material, S = stick)

The material is planks, stone tool materials, iron ingot, gold ingot or diamond. The wooden club uses a **log**.

```
Dagger   Sabre    Scimitar  Spear    Pike     Lance    Halberd  Hatchet  War Hammer  Club
M        ..M      .MM       ..M      .MM      ..M      MMM      MM       MMM         .M
S        .M.      .M.       .S.      .SM      MM.      .S.      MS       MSM         S.
         S..      S..       S..      S..      S..      S..               .S.
```

## Layout

- `item/WeaponType` holds the per-weapon stats. `item/WeaponMaterial` holds the tiers. Edit these to rebalance.
- `item/WeaponItem` is the single item class. It behaves like a sword without the sweep attack.
- `item/WeaponAbility` is the hook point for future abilities. Every weapon uses `NONE` for now.
- `datagen/` generates models, lang, recipes and tags into `src/main/generated`.
- Textures in `assets/weaponry/textures/item` are copied from `../pixel-art/weaponry/png/<tier>/` (render with `python render.py weaponry` there; `warhammer.png` → `<tier>_war_hammer.png`, `gold/` → `golden_`).

## Testing

`config/weaponry.json` (written on first launch):

```json
{ "testChest": true }
```

When `testChest` is on, every world load places a named double chest ("Weaponry Test Chest") 2 blocks
east of world spawn, holding one of each of the 50 weapons. Later launches refill that same chest and clear anything else in it. It defaults
to **on in the dev environment** (`runClient`/`runServer`, config in `run/config/`) and **off** in a normally
installed jar, so the code can stay in releases. Weapons have durability, so they stack to 1 and the chest holds one of each.

Commands: `/give @s weaponry:<material>_<weapon>`, e.g. `weaponry:iron_war_hammer`, `weaponry:golden_dagger`.

## Commands

```
./gradlew runDatagen   # regenerate src/main/generated after changing items
./gradlew runClient    # play-test
./gradlew build        # jar in build/libs
```
