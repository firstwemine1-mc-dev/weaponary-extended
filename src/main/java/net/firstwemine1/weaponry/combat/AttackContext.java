package net.firstwemine1.weaponry.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;

/**
 * State of the melee attack a player is making right now, captured by {@code PlayerMixin} at the
 * start of {@code Player.attack}. Vanilla resets the attack charge before the item hooks run, so
 * abilities read it from here. Mobs always count as fully charged.
 */
public interface AttackContext {
	/** Charge at which vanilla counts a hit as "strong" (sprint knockback, crits, sweeps). */
	float FULL_CHARGE = 0.9f;

	float weaponry$charge();

	boolean weaponry$sprintHit();

	boolean weaponry$forcedCrit();

	void weaponry$setForcedCrit(boolean forced);

	boolean weaponry$crit();

	void weaponry$setCrit(boolean crit);

	static float charge(LivingEntity attacker) {
		return attacker instanceof AttackContext context ? context.weaponry$charge() : 1.0f;
	}

	static boolean fullCharge(LivingEntity attacker) {
		return charge(attacker) > FULL_CHARGE;
	}

	/** Sprinting when the attack started (vanilla stops the sprint before the item hooks run). */
	static boolean sprintHit(LivingEntity attacker) {
		return attacker instanceof AttackContext context ? context.weaponry$sprintHit() : attacker.isSprinting();
	}

	/** An ability turned this hit into a critical hit; play the crit effects for it. */
	static boolean forcedCrit(LivingEntity attacker) {
		return attacker instanceof AttackContext context && context.weaponry$forcedCrit();
	}

	static void forceCrit(LivingEntity attacker) {
		if (attacker instanceof AttackContext context) {
			context.weaponry$setForcedCrit(true);
		}
	}

	/** Records that this hit is a critical hit (vanilla or forced), for abilities that trigger on crits. */
	static void markCrit(LivingEntity attacker) {
		if (attacker instanceof AttackContext context) {
			context.weaponry$setCrit(true);
		}
	}

	/** Set by an ability's damage hook earlier in the same attack; mobs never crit. */
	static boolean crit(LivingEntity attacker) {
		return attacker instanceof AttackContext context && context.weaponry$crit();
	}

	/** The vanilla jump-crit condition from {@code Player.attack}, evaluated at the same moment. */
	static boolean vanillaCrit(Player player, Entity target) {
		return fullCharge(player)
				&& player.fallDistance > 0.0f
				&& !player.onGround()
				&& !player.onClimbable()
				&& !player.isInWater()
				&& !player.hasEffect(MobEffects.BLINDNESS)
				&& !player.isPassenger()
				&& target instanceof LivingEntity
				&& !player.isSprinting();
	}
}
