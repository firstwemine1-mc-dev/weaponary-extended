package net.firstwemine1.weaponry.effect;

import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.GolemEntity;

/**
 * Bleed: {@code amplifier + 1} damage every second, ignoring armor. Each new application adds a
 * stack (up to 3) and refreshes the duration. Undead and golems have no blood and are immune.
 */
public class BleedEffect extends Effect {
	public static final int MAX_STACKS = 3;
	private static final int TICKS_PER_DAMAGE = 20;
	private static final int BASE_DURATION = 80;

	BleedEffect() {
		super(EffectType.HARMFUL, 0xC8161D);
	}

	@Override
	public boolean isDurationEffectTick(int remainingTicks, int amplifier) {
		return remainingTicks % TICKS_PER_DAMAGE == 0;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		// Ignore the hit cooldown, so a tick still lands right after a melee hit (no damage type tags here).
		entity.invulnerableTime = 0;
		entity.hurt(ModEffects.BLEED_DAMAGE, amplifier + 1);
	}

	public static boolean isImmune(LivingEntity entity) {
		return entity.getMobType() == CreatureAttribute.UNDEAD || entity instanceof GolemEntity;
	}

	/**
	 * Duration from the design sheet's material twists: 4 s, wooden 2 s (a crude edge bleeds half as
	 * much), diamond 6 s, golden 6 s (abilities x1.5).
	 */
	public static int duration(WeaponMaterial material) {
		switch (material) {
			case WOODEN:
				return BASE_DURATION / 2;
			case DIAMOND:
				return BASE_DURATION + 40;
			default:
				return Math.round(BASE_DURATION * material.abilityStrength());
		}
	}

	/** Adds one stack of bleed (or refreshes it at the cap). */
	public static void apply(LivingEntity target, LivingEntity source, WeaponMaterial material) {
		if (isImmune(target) || target.level.isClientSide()) {
			return;
		}
		EffectInstance current = target.getEffect(ModEffects.BLEED.get());
		int amplifier = current == null ? 0 : Math.min(MAX_STACKS - 1, current.getAmplifier() + 1);
		target.addEffect(new EffectInstance(ModEffects.BLEED.get(), duration(material), amplifier));
	}
}
