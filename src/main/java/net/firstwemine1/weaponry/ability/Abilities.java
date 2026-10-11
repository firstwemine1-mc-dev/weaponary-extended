package net.firstwemine1.weaponry.ability;

import net.firstwemine1.weaponry.combat.Motion;
import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Event wiring for abilities, plus small helpers they share. */
public final class Abilities {
	private Abilities() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			Motion.tick(server);
			server.getPlayerList().getPlayers().forEach(TwoHanded::tick);
		});

		UseItemCallback.EVENT.register(TwoHanded::onUseItem);
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> TwoHanded.onUse(player, hand));
		UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> TwoHanded.onUse(player, hand));
	}

	/** Puts every material of a weapon type on cooldown, so swapping tiers doesn't skip it. */
	static void cooldown(Player player, WeaponType type, int ticks) {
		for (WeaponMaterial material : WeaponMaterial.values()) {
			player.getCooldowns().addCooldown(ModItems.get(type, material), ticks);
		}
	}

	/** Knock {@code target} back along the attacker's facing, the way vanilla melee knockback does. */
	static void knockbackFrom(LivingEntity target, Entity attacker, double strength) {
		float yaw = attacker.yRot * ((float) Math.PI / 180.0f);
		target.knockback((float) strength, Mth.sin(yaw), -Mth.cos(yaw));
	}
}
