package net.firstwemine1.weaponry.effect;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Status effects applied by weapons. Icons live in {@code textures/mob_effect/<name>.png}, copied
 * from {@code ../pixel-art/weaponry/png/effects/} with a 1 px border (see {@code scripts/copy_effect_icons.py}).
 */
public final class ModEffects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Weaponry.MOD_ID);

	/**
	 * Bleed ticks: ignore armor. Damage types aren't data-driven before 1.19.4, so this is a plain
	 * {@code DamageSource}; its message id gives the "death.attack.weaponry.bleed" death message.
	 * Hit cooldown is skipped in {@link BleedEffect#applyEffectTick} instead of by a tag.
	 */
	public static final DamageSource BLEED_DAMAGE = new DamageSource("weaponry.bleed").bypassArmor();

	/** 1 damage per second per stack, ignoring armor. Stacks up to 3. */
	public static final RegistryObject<MobEffect> BLEED = EFFECTS.register("bleed", BleedEffect::new);

	/** A staggering hit: a staggered player's view lurches (client side, see WeaponryClient). No slowdown. */
	public static final RegistryObject<MobEffect> STAGGER = EFFECTS.register("stagger", () -> new WeaponEffect(MobEffectCategory.HARMFUL, 0x3C8EE0));

	/** Knocked senseless: 90% slower, nearly rooted in place. */
	public static final RegistryObject<MobEffect> STUN = EFFECTS.register("stun",
			() -> new WeaponEffect(MobEffectCategory.HARMFUL, 0xFFD52E)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, "3e8b6a52-1c4d-4f5e-8a9b-7c6d5e4f3a21", -0.9,
							AttributeModifier.Operation.MULTIPLY_TOTAL));

	/** Dazed: 15% slower, like Slowness I; a dazed player's view wobbles (client side, see WeaponryClient). */
	public static final RegistryObject<MobEffect> DAZE = EFFECTS.register("daze",
			() -> new WeaponEffect(MobEffectCategory.HARMFUL, 0xC56CF0)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, "9a1b2c3d-4e5f-4a6b-8c7d-0e1f2a3b4c5d", -0.15,
							AttributeModifier.Operation.MULTIPLY_TOTAL));

	private ModEffects() {
	}

	public static void register(IEventBus modBus) {
		EFFECTS.register(modBus);
	}

	/** MobEffect's constructor is protected; this is a plain effect with only attribute modifiers. */
	static class WeaponEffect extends MobEffect {
		WeaponEffect(MobEffectCategory category, int color) {
			super(category, color);
		}
	}
}
