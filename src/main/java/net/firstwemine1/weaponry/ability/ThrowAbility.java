package net.firstwemine1.weaponry.ability;

import net.firstwemine1.weaponry.entity.ThrownWeapon;
import net.firstwemine1.weaponry.item.WeaponAbility;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.world.World;

/**
 * Trident-style charged throw: hold use for at least half a second, release to throw. The weapon
 * flies as a {@link ThrownWeapon}, costs 1 durability and drops where it lands.
 */
class ThrowAbility implements WeaponAbility {
	private static final int USE_DURATION = 72000;
	private static final int MIN_CHARGE_TICKS = 10;
	/** Design sheet "material twist": diamond throws hit 1 harder. */
	private static final float DIAMOND_THROW_BONUS = 1.0f;

	private final float throwDamageBonus;
	private final float power;

	/**
	 * @param throwDamageBonus added to the weapon's full-charge melee damage
	 * @param power            launch speed; the trident uses 2.5
	 */
	ThrowAbility(float throwDamageBonus, float power) {
		this.throwDamageBonus = throwDamageBonus;
		this.power = power;
	}

	@Override
	public ActionResult<ItemStack> onUse(World level, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (tooDamagedToThrow(stack)) {
			return ActionResult.fail(stack);
		}
		player.startUsingItem(hand);
		return ActionResult.consume(stack);
	}

	@Override
	public UseAction useAnimation() {
		return UseAction.SPEAR;
	}

	@Override
	public int useDuration() {
		return USE_DURATION;
	}

	@Override
	public void onReleaseUsing(ItemStack stack, World level, LivingEntity user, int remainingTicks) {
		if (!(user instanceof PlayerEntity) || !(stack.getItem() instanceof WeaponItem)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) user;
		WeaponItem weapon = (WeaponItem) stack.getItem();
		if (USE_DURATION - remainingTicks < MIN_CHARGE_TICKS || tooDamagedToThrow(stack)) {
			return;
		}
		if (!level.isClientSide()) {
			Hand hand = player.getUsedItemHand();
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			float damage = weapon.attackDamage() + throwDamageBonus
					+ (weapon.material() == WeaponMaterial.DIAMOND ? DIAMOND_THROW_BONUS : 0);
			ThrownWeapon thrown = new ThrownWeapon(level, player, stack, damage);
			thrown.shootFromRotation(player, player.xRot, player.yRot, 0.0f, power, 1.0f);
			level.addFreshEntity(thrown);
			level.playSound(null, thrown, SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
			if (!player.abilities.instabuild) {
				player.inventory.removeItem(stack);
			}
		}
		player.awardStat(Stats.ITEM_USED.get(weapon));
	}

	private static boolean tooDamagedToThrow(ItemStack stack) {
		return stack.getDamageValue() >= stack.getMaxDamage() - 1;
	}
}
