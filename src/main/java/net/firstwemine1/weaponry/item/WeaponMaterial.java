package net.firstwemine1.weaponry.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

/**
 * The material tiers every weapon comes in. Damage bonus, enchantability, repair item and base
 * durability all come from the matching vanilla {@link Tiers}, so weapons track vanilla balance.
 *
 * <p>To add a tier (e.g. netherite): add a constant here, a texture per weapon under
 * {@code textures/item/}, and a crafting/smithing entry in the recipe provider.
 */
public enum WeaponMaterial {
	WOODEN("wooden", "Wooden", Tiers.WOOD),
	STONE("stone", "Stone", Tiers.STONE),
	IRON("iron", "Iron", Tiers.IRON),
	GOLDEN("golden", "Golden", Tiers.GOLD),
	DIAMOND("diamond", "Diamond", Tiers.DIAMOND);

	private final String id;
	private final String displayName;
	private final Tier tier;

	WeaponMaterial(String id, String displayName, Tier tier) {
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

	public Tier tier() {
		return tier;
	}
}
