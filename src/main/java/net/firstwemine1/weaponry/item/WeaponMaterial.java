package net.firstwemine1.weaponry.item;

import net.minecraft.world.item.ToolMaterial;

/**
 * The material tiers every weapon comes in. Damage bonus, enchantability, repair item and base
 * durability all come from the matching vanilla {@link ToolMaterial}, so weapons track vanilla balance.
 *
 * <p>To add a tier (e.g. netherite): add a constant here, a texture per weapon under
 * {@code textures/item/}, and a crafting/smithing entry in the recipe provider.
 */
public enum WeaponMaterial {
	WOODEN("wooden", "Wooden", ToolMaterial.WOOD),
	STONE("stone", "Stone", ToolMaterial.STONE),
	IRON("iron", "Iron", ToolMaterial.IRON),
	GOLDEN("golden", "Golden", ToolMaterial.GOLD),
	DIAMOND("diamond", "Diamond", ToolMaterial.DIAMOND);

	private final String id;
	private final String displayName;
	private final ToolMaterial tier;

	WeaponMaterial(String id, String displayName, ToolMaterial tier) {
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

	public ToolMaterial tier() {
		return tier;
	}
}
