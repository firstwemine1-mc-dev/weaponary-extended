package net.firstwemine1.weaponry.item;

import java.util.List;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.Level;

/**
 * A melee weapon: one {@link WeaponType} in one {@link WeaponMaterial}. Behaves like a sword minus
 * the sweep attack (sweeping is a per-weapon ability, not a default). Vanilla only sweeps with items
 * in the {@code #minecraft:swords} tag, and weapons are deliberately left out of it.
 */
public class WeaponItem extends Item {
	/** Vanilla sword damage is 1 (player base) + 3 + tier bonus. */
	private static final int SWORD_BASE_DAMAGE = 3;
	private static final float PLAYER_BASE_ATTACK_SPEED = 4.0f;
	private static final double PLAYER_BASE_REACH = 3.0;
	private static final Identifier REACH_MODIFIER_ID = Weaponry.id("weapon_reach");
	/**
	 * Not a tool: no fast-mined blocks, breaking a block costs 2 durability and creative players
	 * can't break blocks with it, like a sword.
	 */
	private static final Tool TOOL = new Tool(List.of(), 1.0f, 2, false);
	/** Each hit costs 1 durability, like a sword. */
	private static final Weapon WEAPON = new Weapon(1);

	private final WeaponType type;
	private final WeaponMaterial material;

	public WeaponItem(WeaponType type, WeaponMaterial material, Properties properties) {
		this(type, material, new WeaponTier(material.tier(), type.durabilityMultiplier()), properties);
	}

	private WeaponItem(WeaponType type, WeaponMaterial material, WeaponTier tier, Properties properties) {
		super(properties
				.durability(tier.getUses())
				.repairable(tier.getRepairItems())
				.enchantable(tier.getEnchantmentValue())
				.attributes(createAttributes(type, tier))
				.component(DataComponents.TOOL, TOOL)
				.component(DataComponents.WEAPON, WEAPON));
		this.type = type;
		this.material = material;
	}

	public static ItemAttributeModifiers createAttributes(WeaponType type, WeaponTier tier) {
		// Modifier on top of the player's base 1 damage; never below 0 so a hit always does >= 1.
		float damage = Math.max(0, SWORD_BASE_DAMAGE + tier.getAttackDamageBonus() + type.damageOffset());
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

	// --- ability hooks ---
	// Durability per hit and per block comes from the WEAPON and TOOL components above.

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		type.ability().onHit(stack, target, attacker);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		InteractionResult result = type.ability().onUse(level, player, hand);
		return result.consumesAction() ? result : super.use(level, player, hand);
	}
}
