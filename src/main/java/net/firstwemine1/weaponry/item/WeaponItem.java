package net.firstwemine1.weaponry.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A melee weapon: one {@link WeaponType} in one {@link WeaponMaterial}. Behaves like a sword minus
 * the sweep attack (sweeping is a per-weapon ability, not a default).
 *
 * <p>Not a {@code SwordItem}: in this version {@code Player.attack} sweeps with anything that is
 * one. Sword enchantments are allowed through {@code WeaponEnchantmentCategoryMixin} instead.
 * Reach is not applied: the {@code entity_interaction_range} attribute only exists from 1.20.5.
 */
public class WeaponItem extends TieredItem {
	/** Vanilla sword damage is 1 (player base) + 3 + tier bonus. */
	private static final int SWORD_BASE_DAMAGE = 3;
	private static final float PLAYER_BASE_ATTACK_SPEED = 4.0f;

	private final WeaponType type;
	private final WeaponMaterial material;
	private final Multimap<Attribute, AttributeModifier> attributes;

	public WeaponItem(WeaponType type, WeaponMaterial material, Properties properties) {
		this(type, material, new WeaponTier(material.tier(), type.durabilityMultiplier()), properties);
	}

	private WeaponItem(WeaponType type, WeaponMaterial material, WeaponTier tier, Properties properties) {
		super(tier, properties);
		this.type = type;
		this.material = material;
		this.attributes = createAttributes(type, tier);
	}

	public static Multimap<Attribute, AttributeModifier> createAttributes(WeaponType type, WeaponTier tier) {
		// Modifier on top of the player's base 1 damage; never below 0 so a hit always does >= 1.
		float damage = Math.max(0, SWORD_BASE_DAMAGE + tier.getAttackDamageBonus() + type.damageOffset());
		return ImmutableMultimap.<Attribute, AttributeModifier>builder()
				.put(Attributes.ATTACK_DAMAGE,
						new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION))
				.put(Attributes.ATTACK_SPEED,
						new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", type.attackSpeed() - PLAYER_BASE_ATTACK_SPEED,
								AttributeModifier.Operation.ADDITION))
				.build();
	}

	public WeaponType type() {
		return type;
	}

	public WeaponMaterial material() {
		return material;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? attributes : super.getDefaultAttributeModifiers(slot);
	}

	// --- sword-like durability handling ---

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		type.ability().onHit(stack, target, attacker);
		stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}

	/** Not a tool: no fast-mined blocks, and breaking a block costs 2 durability, like a sword. */
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getDestroySpeed(level, pos) != 0.0f) {
			stack.hurtAndBreak(2, miner, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}

	// --- ability hooks ---

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		InteractionResultHolder<ItemStack> result = type.ability().onUse(level, player, hand);
		return result.getResult().consumesAction() ? result : super.use(level, player, hand);
	}
}
