package net.firstwemine1.weaponry.effect;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Status effects applied by weapons. Icons live in {@code textures/mob_effect/<name>.png}, copied
 * from {@code ../pixel-art/weaponry/png/effects/} with a 1 px border (see {@code scripts/copy_effect_icons.py}).
 */
public final class ModEffects {
	private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Weaponry.MOD_ID);

	/**
	 * Bleed ticks: ignore armor. Damage types aren't data-driven before 1.19.4, so this is a plain
	 * {@code DamageSource}; its message id gives the "death.attack.weaponry.bleed" death message.
	 * Hit cooldown is skipped in {@link BleedEffect#applyEffectTick} instead of by a tag.
	 */
	public static final DamageSource BLEED_DAMAGE = new BleedDamageSource();

	/** 1 damage per second per stack, ignoring armor. Stacks up to 3. */
	public static final RegistryObject<Effect> BLEED = EFFECTS.register("bleed", BleedEffect::new);

	/** A staggering hit: a staggered player's view lurches (client side, see WeaponryClient). No slowdown. */
	public static final RegistryObject<Effect> STAGGER = EFFECTS.register("stagger", () -> new WeaponEffect(EffectType.HARMFUL, 0x3C8EE0));

	/** Knocked senseless: 90% slower, nearly rooted in place. */
	public static final RegistryObject<Effect> STUN = EFFECTS.register("stun",
			() -> new WeaponEffect(EffectType.HARMFUL, 0xFFD52E)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, "3e8b6a52-1c4d-4f5e-8a9b-7c6d5e4f3a21", -0.9,
							AttributeModifier.Operation.MULTIPLY_TOTAL));

	/** Dazed: 15% slower, like Slowness I; a dazed player's view wobbles (client side, see WeaponryClient). */
	public static final RegistryObject<Effect> DAZE = EFFECTS.register("daze",
			() -> new WeaponEffect(EffectType.HARMFUL, 0xC56CF0)
					.addAttributeModifier(Attributes.MOVEMENT_SPEED, "9a1b2c3d-4e5f-4a6b-8c7d-0e1f2a3b4c5d", -0.15,
							AttributeModifier.Operation.MULTIPLY_TOTAL));

	private ModEffects() {
	}

	public static void register(IEventBus modBus) {
		EFFECTS.register(modBus);
	}

	/** DamageSource's constructor and {@code bypassArmor()} are protected in this version. */
	private static class BleedDamageSource extends DamageSource {
		BleedDamageSource() {
			super("weaponry.bleed");
			bypassArmor();
		}
	}

	/** MobEffect's constructor is protected; this is a plain effect with only attribute modifiers. */
	static class WeaponEffect extends Effect {
		WeaponEffect(EffectType category, int color) {
			super(category, color);
		}
	}
}
