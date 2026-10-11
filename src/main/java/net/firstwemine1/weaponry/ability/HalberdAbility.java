package net.firstwemine1.weaponry.ability;

import java.util.List;

import net.firstwemine1.weaponry.item.WeaponAbility;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
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
	public InteractionResultHolder<ItemStack> onUse(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		LivingEntity target = findTarget(player);
		if (target == null) {
			return InteractionResultHolder.pass(stack);
		}
		if (!level.isClientSide()) {
			hook(player, target, stack.getItem() instanceof WeaponItem weapon ? weapon.abilityStrength() : 1.0f);
			stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			Abilities.cooldown(player, WeaponType.HALBERD, COOLDOWN);
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}

	private static void hook(Player player, LivingEntity target, float strength) {
		target.stopRiding();
		if (target instanceof Player victim && victim.isBlocking()) {
			victim.disableShield(true);
		}
		double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
		Vec3 pull = player.position().subtract(target.position()).multiply(1, 0, 1);
		if (pull.lengthSqr() > 1.0e-6 && resistance < 1.0) {
			pull = pull.normalize().scale(PULL_SPEED * strength * (1.0 - resistance));
			target.push(pull.x, PULL_LIFT, pull.z);
			target.hurtMarked = true;
		}
		player.level.playSound(null, target.getX(), target.getY(), target.getZ(),
				SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0f, 0.6f);
	}

	/**
	 * The living entity under the crosshair within the halberd's reach (4 blocks), not through walls.
	 * There is no reach attribute in this version, so the hook uses the weapon's listed reach.
	 */
	private static LivingEntity findTarget(Player player) {
		double range = WeaponType.HALBERD.reach();
		Vec3 eye = player.getEyePosition();
		Vec3 end = eye.add(player.getViewVector(1.0f).scale(range));
		HitResult block = player.level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
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
