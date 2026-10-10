# Weaponry

Fabric mod for Minecraft **1.21.11** (Java 21, Mojang mappings). It adds 10 melee weapons in
5 material tiers, for 50 items in total. This first pass covers base stats only; abilities come later.

Built from the official [fabric-example-mod](https://github.com/FabricMC/fabric-example-mod)
template (`1.21.11` branch). A pristine copy of that template lives at `../fabric-example-mod`.

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
durability all come from the vanilla tool material. Source: `../pixel-art/weaponry/docs/weapon-stats.md`.

\* Recorded in `WeaponType`, but not enforced yet.

## Recipes (M = material, S = stick)

The material is planks, stone tool materials, iron ingot, gold ingot or diamond. The wooden club uses a **log**.

```
Dagger   Sabre    Scimitar  Spear    Pike     Lance    Halberd  Hatchet  War Hammer  Club
M        ..M      .MM       .M       .MM      ..M      MM.      MM       MMM         .M
S        .M.      .M.       .S       .SM      MM.      MS.      MS       MSM         S.
         S..      S..       S.       S..      S..      ..S               .S.
```

## Layout

- `item/WeaponType` holds the per-weapon stats. `item/WeaponMaterial` holds the tiers. Edit these to rebalance.
- `item/WeaponItem` is the single item class. It behaves like a sword without the sweep attack.
- `item/WeaponAbility` is the hook point for future abilities. Every weapon uses `NONE` for now.
- `datagen/` (in the client source set, `src/client/java`) generates models, item model definitions, lang, recipes and tags into `src/main/generated`.
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

## Version notes (1.21.11)

Ported from the 1.21.1 baseline through 1.21.4. Stats, tags, creative tab, config and test chest are unchanged.
Differences from 1.21.1:

- `Tier`/`Tiers` became the `ToolMaterial` record, and `TieredItem` and `SwordItem` are gone. `WeaponMaterial`
  holds a `ToolMaterial`. `WeaponTier` scales its durability and applies durability, repair tag and
  enchantability to the item properties. `WeaponItem` extends `Item` and sets the components a sword gets
  from `Item.Properties.sword(...)`, except for the sword mining rules and the attribute values:
  - `weapon` component (1 durability per hit). This replaces the old `postHurtEnemy` override.
  - `tool` component with no rules, 2 durability per block and `can_destroy_blocks_in_creative: false`.
    This replaces the old `canAttackBlock` override.
  - `hurtEnemy` now returns `void`. It still forwards to `WeaponAbility.onHit`.
- Items need their registry key in `Item.Properties.setId(...)` before they are constructed (`ModItems`).
- `ResourceLocation` is now `Identifier`.
- Attribute ids lost their `generic.`/`player.` prefixes (`minecraft:attack_damage`,
  `minecraft:entity_interaction_range`). The code uses the same `Attributes` constants. Reach still comes
  from the `entity_interaction_range` modifier. The weapons have no `attack_range` component, so vanilla
  uses the attribute.
- `use()` returns `InteractionResult` instead of `InteractionResultHolder`, so `WeaponAbility.onUse` does too.
- **No sweep:** vanilla only sweeps with items in `#minecraft:swords`, and the weapons are not in that tag.
- **Enchanting:** `#minecraft:enchantable/sword` was split into `enchantable/melee_weapon` (knockback,
  looting, and the primary items for sharpness and the other damage enchantments) and `enchantable/sweeping`
  (Sweeping Edge). The weapons are in `melee_weapon`, `sharp_weapon`, `weapon`, `fire_aspect` and `durability`.
  They are not in `sweeping`, so Sweeping Edge, which never did anything on these weapons, no longer applies.
- **Spear recipe:** vanilla 1.21.11 added spears with the old Weaponry spear shape (`..M / .S. / S..`), so
  the Weaponry spear now uses `.M / .S / S.`.
- Item model definitions (`assets/weaponry/items/*.json`) are generated by datagen. Model generation is
  client code, so the `datagen` package lives in the client source set, and `build.gradle` sets
  `configureDataGeneration { client = true }`.
- Datagen APIs: recipes go through a `RecipeProvider` instance (`createRecipeProvider`), and tags use
  `valueLookupBuilder`. Vanilla now files combat recipes under the "equipment" recipe book category.
- Repair items come from the tool material's tag (`#minecraft:iron_tool_materials` and so on). These are the same items as before.
- `TestChest`: the world spawn comes from `getRespawnData().pos()`, and the height bounds come from `getMinY()`/`getMaxY()`.
  Since 1.21.9 there are no always-loaded spawn chunks. The chest is still placed and refilled on server start,
  because the server loads the chunk when the chest is placed.
