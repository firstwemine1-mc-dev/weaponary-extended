package net.firstwemine1.weaponry.item;

import java.util.List;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A melee weapon: one {@link WeaponType} in one {@link WeaponMaterial}. Behaves like a sword minus
 * the sweep attack (sweeping is a per-weapon ability, not a default).
 */
public class WeaponItem extends Item {
	/** Vanilla sword damage is 1 (player base) + 3 + tier bonus. */
	private static final int SWORD_BASE_DAMAGE = 3;
	private static final float PLAYER_BASE_ATTACK_SPEED = 4.0f;
	private static final double PLAYER_BASE_REACH = 3.0;
	private static final ResourceLocation REACH_MODIFIER_ID = Weaponry.id("weapon_reach");
	/** Not a tool: no fast-mined blocks, and breaking a block costs 2 durability, like a sword. */
	private static final Tool TOOL = new Tool(List.of(), 1.0f, 2);

	private final WeaponType type;
	private final WeaponMaterial material;

	public WeaponItem(WeaponType type, WeaponMaterial material, Properties properties) {
		this(type, material, new WeaponTier(material.tier(), type.durabilityMultiplier()), properties);
	}

	private WeaponItem(WeaponType type, WeaponMaterial material, WeaponTier tier, Properties properties) {
		super(tier.applyCommonProperties(properties)
				.attributes(createAttributes(type, tier))
				.component(DataComponents.TOOL, TOOL));
		this.type = type;
		this.material = material;
	}

	public static ItemAttributeModifiers createAttributes(WeaponType type, WeaponTier tier) {
		// Modifier on top of the player's base 1 damage; never below 0 so a hit always does >= 1.
		float damage = Math.max(0, SWORD_BASE_DAMAGE + tier.attackDamageBonus() + type.damageOffset());
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE,
						new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED,
						new AttributeModifier(BASE_ATTACK_SPEED_ID, type.attackSpeed() - PLAYER_BASE_ATTACK_SPEED,
								AttributeModifier.Operation.ADD_VALUE),
						EquipmentSlotGroup.MAINHAND);
		double reachBonus = type.reach() - PLAYER_BASE_REACH;
		if (reachBonus != 0) {
			builder.add(Attributes.ENTITY_INTERACTION_RANGE,
					new AttributeModifier(REACH_MODIFIER_ID, reachBonus, AttributeModifier.Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND);
		}
		return builder.build();
	}

	public WeaponType type() {
		return type;
	}

	public WeaponMaterial material() {
		return material;
	}

	// --- sword-like durability handling ---

	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return !player.isCreative();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		type.ability().onHit(stack, target, attacker);
		return true;
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}

	// --- ability hooks ---

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		InteractionResult result = type.ability().onUse(level, player, hand);
		return result.consumesAction() ? result : super.use(level, player, hand);
	}
}
