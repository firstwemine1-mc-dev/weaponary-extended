package net.firstwemine1.weaponry.effect;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Status effects applied by weapons. Icons live in {@code textures/mob_effect/<name>.png}, copied
 * from {@code ../pixel-art/weaponry/png/effects/} with a 1 px border (see {@code scripts/copy_effect_icons.py}).
 */
public final class ModEffects {
	/** Damage type of bleed ticks: ignores armor and hit cooldown ({@code data/weaponry/damage_type/bleed.json}). */
	public static final ResourceKey<DamageType> BLEED_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Weaponry.id("bleed"));

	/** 1 damage per second per stack, ignoring armor. Stacks up to 3. */
	public static final Holder<MobEffect> BLEED = register("bleed", new BleedEffect());

	/** A staggering hit: a staggered player's view lurches (client side, see WeaponryClient). No slowdown. */
	public static final Holder<MobEffect> STAGGER = register("stagger", new WeaponEffect(MobEffectCategory.HARMFUL, 0x3C8EE0));

	/** Knocked senseless: 90% slower, nearly rooted in place. */
	public static final Holder<MobEffect> STUN = register("stun",
			new WeaponEffect(MobEffectCategory.HARMFUL, 0xFFD52E)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, Weaponry.id("effect.stun"), -0.9,
							AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	/** Dazed: 15% slower, like Slowness I; a dazed player's view wobbles (client side, see WeaponryClient). */
	public static final Holder<MobEffect> DAZE = register("daze",
			new WeaponEffect(MobEffectCategory.HARMFUL, 0xC56CF0)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, Weaponry.id("effect.daze"), -0.15,
							AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

	private ModEffects() {
	}

	private static Holder<MobEffect> register(String name, MobEffect effect) {
		return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Weaponry.id(name), effect);
	}

	/** Loads the class, which registers the effects. */
	public static void register() {
	}

	/** MobEffect's constructor is protected; this is a plain effect with only attribute modifiers. */
	static class WeaponEffect extends MobEffect {
		WeaponEffect(MobEffectCategory category, int color) {
			super(category, color);
		}
	}
}
