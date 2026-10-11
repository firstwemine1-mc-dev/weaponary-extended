package net.firstwemine1.weaponry.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemTier;

/**
 * The material tiers every weapon comes in. Damage bonus, enchantability, repair item and base
 * durability all come from the matching vanilla {@link Tiers}, so weapons track vanilla balance.
 *
 * <p>{@link #abilityStrength()} is the gold "material twist" from the design sheet: gold weapons
 * have weaker durability but stronger abilities. The other twists (wooden armor crush, stone
 * knockback, diamond throw damage) live in the abilities that use them.
 *
 * <p>To add a tier (e.g. netherite): add a constant here, a texture per weapon under
 * {@code textures/item/}, and a crafting/smithing entry in the recipe provider.
 */
public enum WeaponMaterial {
	WOODEN("wooden", "Wooden", ItemTier.WOOD),
	STONE("stone", "Stone", ItemTier.STONE),
	IRON("iron", "Iron", ItemTier.IRON),
	GOLDEN("golden", "Golden", ItemTier.GOLD),
	DIAMOND("diamond", "Diamond", ItemTier.DIAMOND);

	private final String id;
	private final String displayName;
	private final IItemTier tier;

	WeaponMaterial(String id, String displayName, IItemTier tier) {
		this.id = id;
		this.displayName = displayName;
		this.tier = tier;
	}

	public String id() {
		return id;
	}

	public String displayName() {
		return displayName;
	}

	public IItemTier tier() {
		return tier;
	}

	/** Multiplier for ability numbers (bonus damage, effect durations, chances, pull strength). */
	public float abilityStrength() {
		return this == GOLDEN ? 1.5f : 1.0f;
	}
}
