package net.firstwemine1.weaponry.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * A vanilla tool material with durability scaled for one weapon type. Items take durability,
 * repair item and enchantability from properties, which {@link #applyCommonProperties} sets.
 */
public record WeaponTier(ToolMaterial base, float durabilityMultiplier) {
	public int durability() {
		return Math.max(1, (int) (base.durability() * durabilityMultiplier));
	}

	public float attackDamageBonus() {
		return base.attackDamageBonus();
	}

	/** Same as the private {@code ToolMaterial.applyCommonProperties}, with the scaled durability. */
	public Item.Properties applyCommonProperties(Item.Properties properties) {
		return properties.durability(durability())
				.repairable(base.repairItems())
				.enchantable(base.enchantmentValue());
	}
}
