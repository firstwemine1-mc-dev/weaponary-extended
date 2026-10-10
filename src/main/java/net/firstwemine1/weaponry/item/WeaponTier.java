package net.firstwemine1.weaponry.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

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
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return base.getIncorrectBlocksForDrops();
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
