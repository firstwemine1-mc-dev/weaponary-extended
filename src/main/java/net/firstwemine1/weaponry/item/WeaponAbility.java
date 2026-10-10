package net.firstwemine1.weaponry.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Extension point for weapon abilities (backstab, throw, brace, hook, ...). Every weapon currently
 * uses {@link #NONE}. To add one, implement the hooks you need and pass the instance to the
 * {@link WeaponType} constant; {@link WeaponItem} already forwards to these methods.
 */
public interface WeaponAbility {
	WeaponAbility NONE = new WeaponAbility() {
	};

	/** Called server-side after a melee hit lands. */
	default void onHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
	}

	/** Right-click. Return {@code pass} to fall through to vanilla behaviour. */
	default InteractionResultHolder<ItemStack> onUse(Level level, Player player, InteractionHand hand) {
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}
}
