# Weaponry

Fabric mod for Minecraft **1.19.2** (Java 17, Mojang mappings). It adds 10 melee weapons in
5 material tiers, for 50 items in total. This first pass covers base stats only; abilities come later.

Built from the official [fabric-example-mod](https://github.com/FabricMC/fabric-example-mod)
template (`1.19.2` branch). A pristine copy of that template lives at `../fabric-example-mod`.
Ported from the 1.21.1 version; see [Version notes](#version-notes).

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

\* Recorded in `WeaponType`, but not enforced yet. Reach is not applied on 1.19.2 (see [Version notes](#version-notes)).

## Recipes (M = material, S = stick)

The material is planks, stone tool materials, iron ingot, gold ingot or diamond. The wooden club uses a **log**.

```
Dagger   Sabre    Scimitar  Spear    Pike     Lance    Halberd  Hatchet  War Hammer  Club
M        ..M      .MM       ..M      .MM      ..M      MM.      MM       MMM         .M
S        .M.      .M.       .S.      .SM      MM.      MS.      MS       MSM         S.
         S..      S..       S..      S..      S..      ..S               .S.
```

## Layout

- `item/WeaponType` holds the per-weapon stats. `item/WeaponMaterial` holds the tiers. Edit these to rebalance.
- `item/WeaponItem` is the single item class. It behaves like a sword without the sweep attack.
- `mixin/WeaponEnchantmentCategoryMixin` lets weapons take sword enchantments (see [Version notes](#version-notes)).
- `item/WeaponAbility` is the hook point for future abilities. Every weapon uses `NONE` for now.
- `datagen/` generates models, lang and recipes into `src/main/generated`.
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

## Version notes

Differences from the 1.21.1 version:

- **No reach.** The `entity_interaction_range` attribute only exists from 1.20.5, so every weapon has the
  vanilla 3-block reach. The reach values stay in `WeaponType` for later versions.
- **Attributes** come from `getDefaultAttributeModifiers(EquipmentSlot)` with the vanilla
  `BASE_ATTACK_DAMAGE_UUID` / `BASE_ATTACK_SPEED_UUID` modifiers, not from item components.
- **Enchanting.** Enchantments aren't tag-based yet: sharpness, smite, bane of arthropods, knockback, fire
  aspect, looting and sweeping edge use the `WEAPON` category, which only accepts `SwordItem`. Weapons
  aren't swords, because `Player.attack` sweeps with any `SwordItem`. Fabric API has no hook for this here,
  so `WeaponEnchantmentCategoryMixin` makes that category accept weapons. That covers the enchanting table,
  the anvil and `/enchant`. Unbreaking, mending and vanishing work on any damageable item already.
  Sweeping edge can be applied but does nothing, as on 1.21.1.
- **Durability per block** is handled in `WeaponItem.mineBlock` (2 per block, like a sword) instead of the
  `tool` component.
- **Creative tab:** built with `FabricItemGroupBuilder`, and items join it through
  `Item.Properties.tab`. Its translation key is `itemGroup.weaponry.weapons`.
- **Data:** recipes in `data/weaponry/recipes` with `result.item` and no recipe category. No item tags:
  the `enchantable/*` tags and `breaks_decorated_pots` don't exist yet, so there is no tag provider.
  The datagen providers use the older `FabricDataGenerator` constructors.
- `Registry.ITEM` instead of `BuiltInRegistries`, `new ResourceLocation(...)`, and the test chest is
  named with `setCustomName`.
