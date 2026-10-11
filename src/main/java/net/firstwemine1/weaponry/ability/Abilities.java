package net.firstwemine1.weaponry.ability;

import net.firstwemine1.weaponry.combat.Motion;
import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/** Event wiring for abilities, plus small helpers they share. */
public final class Abilities {
	private Abilities() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
			if (event.phase == TickEvent.Phase.END) {
				MinecraftServer server = event.getServer();
				Motion.tick(server);
				server.getPlayerList().getPlayers().forEach(TwoHanded::tick);
			}
		});
		// The sabre parry: cancel a frontal melee hit on a parrying player before it does anything.
		MinecraftForge.EVENT_BUS.addListener((LivingAttackEvent event) -> {
			if (!event.getEntity().level().isClientSide()
					&& !SabreAbility.allowDamage(event.getEntity(), event.getSource(), event.getAmount())) {
				event.setCanceled(true);
			}
		});

		// Two-handed weapons block the off hand (item use, block use, entity use).
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickItem event) -> {
			if (TwoHanded.check(event.getEntity(), event.getHand())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.FAIL);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
			if (TwoHanded.check(event.getEntity(), event.getHand())) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.FAIL);
			}
		});
		MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract event) -> {
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
