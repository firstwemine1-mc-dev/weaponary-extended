package net.firstwemine1.weaponry.ability;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.firstwemine1.weaponry.combat.Motion;
import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Event wiring for abilities, plus small helpers they share. */
public final class Abilities {
	private Abilities() {
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> {
			MinecraftServer server = event.getServer();
			Motion.tick(server);
			server.getPlayerList().getPlayers().forEach(TwoHanded::tick);
		});
		// The sabre parry: cancel a frontal melee hit on a parrying player before it does anything.
		NeoForge.EVENT_BUS.addListener((LivingIncomingDamageEvent event) -> {
			if (!event.getEntity().level().isClientSide()
					&& !SabreAbility.allowDamage(event.getEntity(), event.getSource(), event.getAmount())) {
				event.setCanceled(true);
			}
		});

		// Two-handed weapons block the off hand (item use, block use, entity use).
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickItem event) -> {
			if (TwoHanded.check(event.getEntity(), event.getHand())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.FAIL);
			}
		});
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
			if (TwoHanded.check(event.getEntity(), event.getHand())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.FAIL);
			}
		});
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
			if (TwoHanded.check(event.getEntity(), event.getHand())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.FAIL);
			}
		});
	}

	/** Puts every material of a weapon type on cooldown, so swapping tiers doesn't skip it. */
	static void cooldown(Player player, WeaponType type, int ticks) {
		for (WeaponMaterial material : WeaponMaterial.values()) {
			player.getCooldowns().addCooldown(ModItems.get(type, material), ticks);
		}
	}

	/** Knock {@code target} back along the attacker's facing, the way vanilla melee knockback does. */
	static void knockbackFrom(LivingEntity target, Entity attacker, double strength) {
		float yaw = attacker.getYRot() * Mth.DEG_TO_RAD;
		target.knockback(strength, Mth.sin(yaw), -Mth.cos(yaw));
	}
}
