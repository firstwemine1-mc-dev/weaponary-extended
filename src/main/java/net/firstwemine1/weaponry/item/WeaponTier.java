package net.firstwemine1.weaponry.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * A vanilla tool material with durability scaled for one weapon type. Durability is an item
 * component set from the tier, so scaling it here is how each weapon gets its own durability.
 */
public record WeaponTier(ToolMaterial base, float durabilityMultiplier) {
	public int getUses() {
		return Math.max(1, (int) (base.durability() * durabilityMultiplier));
	}

	public float getAttackDamageBonus() {
		return base.attackDamageBonus();
	}

	public int getEnchantmentValue() {
		return base.enchantmentValue();
	}

	public TagKey<Item> getRepairItems() {
		return base.repairItems();
	}
}
