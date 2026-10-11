package net.firstwemine1.weaponry.entity;

import org.jetbrains.annotations.Nullable;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * A thrown spear, hatchet or dagger. Flies like a trident, deals a fixed damage on its first hit,
 * then drops and lies where it lands until its owner picks it back up. No Loyalty: it never flies back.
 *
 * <p>In this version {@code AbstractArrow} keeps no item stack, so the weapon is stored here (like the
 * vanilla trident does) and synced so the client can render it.
 */
public class ThrownWeapon extends AbstractArrow {
	private static final EntityDataAccessor<ItemStack> DATA_ITEM =
			SynchedEntityData.defineId(ThrownWeapon.class, EntityDataSerializers.ITEM_STACK);

	private ItemStack weapon = ItemStack.EMPTY;
	private float damage;
	private boolean dealtDamage;

	public ThrownWeapon(EntityType<? extends ThrownWeapon> type, Level level) {
		super(type, level);
	}

	public ThrownWeapon(Level level, LivingEntity owner, ItemStack stack, float damage) {
		super(ModEntities.THROWN_WEAPON, owner, level);
		this.weapon = stack.copy();
		this.damage = damage;
		this.entityData.set(DATA_ITEM, weapon.copy());
		if (owner instanceof Player player) {
			this.pickup = player.getAbilities().instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DATA_ITEM, ItemStack.EMPTY);
	}

	/** The weapon, synced so the client can render it. */
	public ItemStack getItem() {
		return entityData.get(DATA_ITEM);
	}

	public boolean isStuck() {
		return inGround;
	}

	@Override
	public void tick() {
		if (inGroundTime > 4) {
			dealtDamage = true;
		}
		super.tick();
	}

	@Nullable
	@Override
	protected EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
		return dealtDamage ? null : super.findHitEntity(from, to);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		Entity target = result.getEntity();
		Entity owner = getOwner();
		float amount = damage;
		if (target instanceof LivingEntity living) {
			amount += EnchantmentHelper.getDamageBonus(weapon, living.getMobType());
		}
		DamageSource source = DamageSource.thrown(this, owner == null ? this : owner);

		dealtDamage = true;
		if (target.hurt(source, amount) && target instanceof LivingEntity living) {
			if (owner instanceof LivingEntity thrower) {
				EnchantmentHelper.doPostHurtEffects(living, thrower);
				EnchantmentHelper.doPostDamageEffects(thrower, living);
				if (!level.isClientSide()) {
					WeaponItem.abilityOf(weapon).onThrownHit(weapon, living, thrower);
				}
			}
			doPostHurtEffects(living);
		}

		// Bounce off and drop, like a trident without Loyalty.
		setDeltaMovement(getDeltaMovement().multiply(-0.01, -0.1, -0.01));
		playSound(SoundEvents.TRIDENT_HIT, 1.0f, 1.0f);
	}

	@Override
	protected ItemStack getPickupItem() {
		return weapon.isEmpty() ? new ItemStack(ModItems.get(WeaponType.SPEAR, WeaponMaterial.IRON)) : weapon.copy();
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
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Weapon", 10)) {
			weapon = ItemStack.of(tag.getCompound("Weapon"));
		}
		damage = tag.getFloat("Damage");
		dealtDamage = tag.getBoolean("DealtDamage");
		entityData.set(DATA_ITEM, weapon.copy());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Weapon", weapon.save(new CompoundTag()));
		tag.putFloat("Damage", damage);
		tag.putBoolean("DealtDamage", dealtDamage);
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
