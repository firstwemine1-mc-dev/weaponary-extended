package net.firstwemine1.weaponry.combat;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import net.firstwemine1.weaponry.ability.SabreAbility;
import net.firstwemine1.weaponry.item.WeaponAbility;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;

/**
 * The player melee hooks that the Fabric branches do with mixins on {@code Player.attack}, done with
 * Forge's events instead. In order, within one attack:
 * <ol>
 *   <li>{@code AttackEntityEvent}: capture the attack charge and sprint ({@link AttackContext}).</li>
 *   <li>{@code CriticalHitEvent}: no crits with the lance; a sabre riposte forces one.</li>
 *   <li>{@code LivingHurtEvent} on the attack's target: the ability damage bonus, and armor crush
 *       lowers the target's armor (it is applied after this event).</li>
 *   <li>{@code LivingDamageEvent}: armor crush puts the armor back.</li>
 * </ol>
 */
public final class CombatEvents {
	private static final UUID ARMOR_CRUSH_UUID = UUID.fromString("b4f7d3c2-8e1a-4b6c-9d2e-3f4a5b6c7d8e");
	private static final float CRIT_MULTIPLIER = 1.5f;

	/** Targets whose armor is crushed for the hit in progress, until LivingDamageEvent restores it. */
	private static final Map<LivingEntity, ModifiableAttributeInstance> CRUSHED = new WeakHashMap<>();

	private CombatEvents() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(CombatEvents::onAttack);
		MinecraftForge.EVENT_BUS.addListener(CombatEvents::onCriticalHit);
		MinecraftForge.EVENT_BUS.addListener(CombatEvents::onHurt);
		MinecraftForge.EVENT_BUS.addListener(CombatEvents::onDamage);
		MinecraftForge.EVENT_BUS.addListener(CombatEvents::onServerTick);
	}

	private static void onAttack(AttackEntityEvent event) {
		AttackContext.begin(event.getPlayer(), event.getTarget());
	}

	private static void onCriticalHit(CriticalHitEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!(player.getMainHandItem().getItem() instanceof WeaponItem)) {
			return;
		}
		WeaponAbility ability = ((WeaponItem) player.getMainHandItem().getItem()).type().ability();
		if (!ability.allowsCrits()) {
			event.setResult(Event.Result.DENY);
			return;
		}
		if (ability instanceof SabreAbility && !event.isVanillaCritical() && event.getTarget() instanceof LivingEntity
				&& !player.level.isClientSide() && SabreAbility.consumeRiposte(player)) {
			event.setResult(Event.Result.ALLOW);
			event.setDamageModifier(CRIT_MULTIPLIER);
		}
		boolean crit = event.getResult() == Event.Result.ALLOW
				|| event.getResult() == Event.Result.DEFAULT && event.isVanillaCritical();
		if (crit) {
			AttackContext.markCrit(player);
		}
	}

	private static void onHurt(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		LivingEntity target = event.getEntityLiving();
		if (!(source.getEntity() instanceof PlayerEntity) || source.getDirectEntity() != source.getEntity()
				|| !"player".equals(source.getMsgId())) {
			return;
		}
		PlayerEntity player = (PlayerEntity) source.getEntity();
		if (player.level.isClientSide() || !(player.getMainHandItem().getItem() instanceof WeaponItem)
				|| !AttackContext.takeMainHit(player, target)) {
			return;
		}
		WeaponItem weapon = (WeaponItem) player.getMainHandItem().getItem();
		WeaponAbility ability = weapon.type().ability();
		float bonus = ability.attackDamageBonus(weapon, player, target, event.getAmount());
		if (bonus != 0) {
			event.setAmount(Math.max(0, event.getAmount() + bonus));
		}

		float pierce = ability.armorPierce(weapon.material());
		ModifiableAttributeInstance armor = target.getAttribute(Attributes.ARMOR);
		if (pierce > 0 && armor != null && armor.getModifier(ARMOR_CRUSH_UUID) == null) {
			armor.addTransientModifier(new AttributeModifier(ARMOR_CRUSH_UUID, "Armor crush", -pierce,
					AttributeModifier.Operation.MULTIPLY_TOTAL));
			CRUSHED.put(target, armor);
		}
	}

	private static void onDamage(LivingDamageEvent event) {
		restoreArmor(event.getEntityLiving());
	}

	/** In case a hit was cancelled between the two events, nothing keeps crushed armor past the tick. */
	private static void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && !CRUSHED.isEmpty()) {
			for (LivingEntity entity : CRUSHED.keySet().toArray(new LivingEntity[0])) {
				restoreArmor(entity);
			}
		}
	}

	private static void restoreArmor(LivingEntity entity) {
		ModifiableAttributeInstance armor = CRUSHED.remove(entity);
		if (armor != null) {
			armor.removeModifier(ARMOR_CRUSH_UUID);
		}
	}
}
