package net.firstwemine1.weaponry.effect;

import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.AbstractGolem;

/**
 * Bleed: {@code amplifier + 1} damage every second, ignoring armor. Each new application adds a
 * stack (up to 3) and refreshes the duration. Undead and golems have no blood and are immune.
 */
public class BleedEffect extends MobEffect {
	public static final int MAX_STACKS = 3;
	private static final int TICKS_PER_DAMAGE = 20;
	private static final int BASE_DURATION = 80;

	BleedEffect() {
		super(MobEffectCategory.HARMFUL, 0xC8161D);
	}

	@Override
	public boolean isDurationEffectTick(int remainingTicks, int amplifier) {
		return remainingTicks % TICKS_PER_DAMAGE == 0;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		entity.hurt(entity.damageSources().source(ModEffects.BLEED_DAMAGE), amplifier + 1);
	}

	public static boolean isImmune(LivingEntity entity) {
		return entity.getMobType() == MobType.UNDEAD || entity instanceof AbstractGolem;
	}

	/**
	 * Duration from the design sheet's material twists: 4 s, wooden 2 s (a crude edge bleeds half as
	 * much), diamond 6 s, golden 6 s (abilities x1.5).
	 */
	public static int duration(WeaponMaterial material) {
		return switch (material) {
			case WOODEN -> BASE_DURATION / 2;
			case DIAMOND -> BASE_DURATION + 40;
			default -> Math.round(BASE_DURATION * material.abilityStrength());
		};
	}

	/** Adds one stack of bleed (or refreshes it at the cap). */
	public static void apply(LivingEntity target, LivingEntity source, WeaponMaterial material) {
		if (isImmune(target) || target.level().isClientSide()) {
			return;
		}
		MobEffectInstance current = target.getEffect(ModEffects.BLEED);
		int amplifier = current == null ? 0 : Math.min(MAX_STACKS - 1, current.getAmplifier() + 1);
		target.addEffect(new MobEffectInstance(ModEffects.BLEED, duration(material), amplifier), source);
	}
}
