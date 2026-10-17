package net.firstwemine1.weaponry.ability;

import java.util.List;

import net.firstwemine1.weaponry.item.WeaponAbility;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Reach 4 (from stats). Shield-breaker like an axe. Hook: use on a target in reach to pull it 1-2
 * blocks toward you, drag it off its mount and yank a raised shield aside.
 */
public class HalberdAbility implements WeaponAbility {
	private static final int COOLDOWN = 40;
	/** Horizontal launch speed toward the player; about 1.5 blocks of travel on the ground. */
	private static final double PULL_SPEED = 0.6;
	private static final double PULL_LIFT = 0.25;

	@Override
	public List<String> description() {
		return List.of(
				"Disables shields",
				"Use: hook a target toward you, pulling riders off their mounts");
	}

	@Override
	public boolean disablesShields() {
		return true;
	}

	@Override
	public InteractionResult onUse(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		LivingEntity target = findTarget(player);
		if (target == null) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			hook(player, target, stack.getItem() instanceof WeaponItem weapon ? weapon.abilityStrength() : 1.0f);
			stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
			Abilities.cooldown(player, WeaponType.HALBERD, COOLDOWN);
		}
		return InteractionResult.SUCCESS;
	}

	private static void hook(Player player, LivingEntity target, float strength) {
		target.stopRiding();
		// Yank a raised shield aside: disable it the way an axe hit does (blocks_attacks component).
		ItemStack shield = target.getItemBlockingWith();
		BlocksAttacks blocking = shield != null ? shield.get(DataComponents.BLOCKS_ATTACKS) : null;
		if (blocking != null && player.level() instanceof ServerLevel serverLevel) {
			blocking.disable(serverLevel, target, Weapon.AXE_DISABLES_BLOCKING_FOR_SECONDS, shield);
		}
		double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
		Vec3 pull = player.position().subtract(target.position()).multiply(1, 0, 1);
		if (pull.lengthSqr() > 1.0e-6 && resistance < 1.0) {
			pull = pull.normalize().scale(PULL_SPEED * strength * (1.0 - resistance));
			target.push(pull.x, PULL_LIFT, pull.z);
			// push() only syncs to watchers; a player moves itself, so its own client needs the motion too.
			target.syncVelocity = true;
		}
		player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
				SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0f, 0.6f);
	}

	/** The living entity under the crosshair within the player's reach, not through walls. */
	private static LivingEntity findTarget(Player player) {
		double range = player.entityInteractionRange();
		Vec3 eye = player.getEyePosition();
		Vec3 end = eye.add(player.getViewVector(1.0f).scale(range));
		HitResult block = player.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
		if (block.getType() != HitResult.Type.MISS) {
			end = block.getLocation();
		}
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, eye, end,
				player.getBoundingBox().expandTowards(end.subtract(eye)).inflate(1.0),
				entity -> entity instanceof LivingEntity && !entity.isSpectator() && entity.isPickable(),
				eye.distanceToSqr(end));
		Entity entity = hit != null ? hit.getEntity() : null;
		return entity instanceof LivingEntity living ? living : null;
	}
}
