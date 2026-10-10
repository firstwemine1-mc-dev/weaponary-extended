# Weaponry

Fabric mod for Minecraft **26.3** (Java 25, unobfuscated). It adds 10 melee weapons in
5 material tiers, for 50 items in total. This first pass covers base stats only; abilities come later.

Built from the official [fabric-example-mod](https://github.com/FabricMC/fabric-example-mod)
template (`26.3` branch). A pristine copy of that template lives at `../fabric-example-mod`.

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
M        ..M      .MM       ..M      .MM      ..M      MM.      MM       MMM         .M
S        .M.      .M.       .SM      .SM      MM.      MS.      MS       MSM         S.
         S..      S..       S..      S..      S..      ..S               .S.
```

## Layout

- `item/WeaponType` holds the per-weapon stats. `item/WeaponMaterial` holds the tiers. Edit these to rebalance.
- `item/WeaponItem` is the single item class. It behaves like a sword without the sweep attack.
- `item/WeaponAbility` is the hook point for future abilities. Every weapon uses `NONE` for now.
- `datagen/` (in `src/client`, because item models are client-only) generates models, item model
  definitions, lang, recipes and tags into `src/main/generated`.
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

## Version notes (26.3)

Ported from the 1.21.1 baseline (via 26.1.2 and 26.2). Stats, recipes (except the spear), tiers, reach, the creative tab, the
config and the test chest are unchanged.

- **Build:** Minecraft 26.x ships unobfuscated: `net.fabricmc.fabric-loom` without a `mappings` line,
  `implementation` instead of `modImplementation`, Java 25. Datagen runs with `client = true`.
- **Items:** `TieredItem`/`Tier` are gone. `WeaponItem` extends `Item` and gets durability, repair item and
  enchantability from the vanilla `ToolMaterial` (durability scaled per weapon), plus `tool` and `weapon`
  components (1 durability per hit, 2 per block, can't break blocks in creative, like a sword).
  Items are registered with `Item.Properties.setId`.
- **No sweep:** vanilla sweeps only when the held item is in `#minecraft:swords`. Weapons aren't in it.
- **Enchanting:** `#minecraft:enchantable/sword` no longer exists. Weapons are in
  `#enchantable/melee_weapon` (Sharpness, Smite, Bane, Knockback, Fire Aspect, Looting at the table) but
  not in `#enchantable/sweeping`, so Sweeping Edge can't be applied (it would do nothing).
- **Item models:** item model definitions (`assets/weaponry/items/*.json`) are generated next to the models.
- **Vanilla spear:** 26.x has `minecraft:*_spear` (added in 1.21.11). Our ids are `weaponry:*_spear`, so
  nothing clashes, but vanilla's recipe uses our old spear shape. The spear recipe now has a wider head
  (`..M / .SM / S..`). Our spear is a plain melee weapon, not vanilla's charge/jab spear.
- **Test chest:** placed next to the world respawn point, in the respawn dimension.
- Attribute ids in commands have no `generic.` prefix (`minecraft:attack_damage`, `minecraft:entity_interaction_range`).
- **26.1.2 -> 26.2:** no gameplay change. Only datagen code moved: advancement criteria are in
  `net.minecraft.advancements.triggers`, and item tags are appended by registry key.
- **26.2 -> 26.3:** no gameplay change. The recipe provider is built from bootstrap contexts for recipes
  and advancements, and recipe-unlock advancements use the new `recipes` field (regenerated).
