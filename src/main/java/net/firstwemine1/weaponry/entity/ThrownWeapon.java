package net.firstwemine1.weaponry.entity;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A thrown spear, hatchet or dagger. Flies like a trident, deals a fixed damage on its first hit,
 * then drops and lies where it lands until its owner picks it back up. No Loyalty: it never flies back.
 */
public class ThrownWeapon extends AbstractArrow {
	private static final EntityDataAccessor<ItemStack> DATA_ITEM =
			SynchedEntityData.defineId(ThrownWeapon.class, EntityDataSerializers.ITEM_STACK);

	private float damage;
	private boolean dealtDamage;

	public ThrownWeapon(EntityType<? extends ThrownWeapon> type, Level level) {
		super(type, level);
	}

	public ThrownWeapon(Level level, LivingEntity owner, ItemStack stack, float damage) {
		super(ModEntities.THROWN_WEAPON, owner, level, stack, null);
		this.damage = damage;
		this.entityData.set(DATA_ITEM, stack.copy());
		if (owner instanceof Player player) {
			this.pickup = player.hasInfiniteMaterials() ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_ITEM, ItemStack.EMPTY);
	}

	/** The weapon, synced so the client can render it. */
	public ItemStack getItem() {
		return entityData.get(DATA_ITEM);
	}

	public boolean isStuck() {
		return isInGround();
	}

	@Override
	public void tick() {
		if (inGroundTime > 4) {
			dealtDamage = true;
		}
		super.tick();
	}

	@Override
	protected @Nullable EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
		return dealtDamage ? null : super.findHitEntity(from, to);
	}

	/** One target per throw, like the trident (no piercing). */
	@Override
	protected Collection<EntityHitResult> findHitEntities(Vec3 from, Vec3 to) {
		EntityHitResult hit = findHitEntity(from, to);
		return hit != null ? List.of(hit) : List.of();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity target = result.getEntity();
		Entity owner = getOwner();
		DamageSource source = damageSources().thrown(this, owner == null ? this : owner);
		float amount = damage;
		if (level() instanceof ServerLevel serverLevel) {
			amount = EnchantmentHelper.modifyDamage(serverLevel, getWeaponItem(), target, source, amount);
		}

		dealtDamage = true;
		if (target.hurtOrSimulate(source, amount)) {
			if (level() instanceof ServerLevel serverLevel) {
				EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(serverLevel, target, source, getWeaponItem(),
						item -> kill(serverLevel));
			}
			if (target instanceof LivingEntity living) {
				doKnockback(living, source);
				doPostHurtEffects(living);
				if (owner instanceof LivingEntity thrower && !level().isClientSide()) {
					WeaponItem.abilityOf(getWeaponItem()).onThrownHit(getWeaponItem(), living, thrower);
				}
			}
		}

		// Bounce off and drop, like a trident without Loyalty.
		deflect(ProjectileDeflection.REVERSE, target, this.owner, false);
		setDeltaMovement(getDeltaMovement().multiply(0.02, 0.2, 0.02));
		playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
	}

	@Override
	public ItemStack getWeaponItem() {
		return getPickupItemStackOrigin();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(ModItems.get(WeaponType.SPEAR, WeaponMaterial.IRON));
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.TRIDENT_HIT_GROUND;
	}

	@Override
	public void playerTouch(Player player) {
		if (ownedBy(player) || getOwner() == null) {
			super.playerTouch(player);
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		damage = input.getFloatOr("Damage", 0.0f);
		dealtDamage = input.getBooleanOr("DealtDamage", false);
		entityData.set(DATA_ITEM, getPickupItemStackOrigin().copy());
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putFloat("Damage", damage);
		output.putBoolean("DealtDamage", dealtDamage);
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
