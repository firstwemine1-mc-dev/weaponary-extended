package net.firstwemine1.weaponry.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A vanilla tier with durability scaled for one weapon type. {@code TieredItem} takes durability
 * from the tier, so scaling it here is how each weapon gets its own durability.
 */
public record WeaponTier(Tier base, float durabilityMultiplier) implements Tier {
	@Override
	public int getUses() {
		return Math.max(1, (int) (base.getUses() * durabilityMultiplier));
	}

	@Override
	public float getSpeed() {
		return base.getSpeed();
	}

	@Override
	public float getAttackDamageBonus() {
		return base.getAttackDamageBonus();
	}

	@Override
	public int getLevel() {
		return base.getLevel();
	}

	@Override
	public int getEnchantmentValue() {
		return base.getEnchantmentValue();
	}

	@Override
	public Ingredient getRepairIngredient() {
		return base.getRepairIngredient();
	}
}
