package net.firstwemine1.weaponry.entity;

import javax.annotation.Nullable;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * A thrown spear, hatchet or dagger. Flies like a trident, deals a fixed damage on its first hit,
 * then drops and lies where it lands until its owner picks it back up. No Loyalty: it never flies back.
 *
 * <p>In this version {@code AbstractArrow} keeps no item stack, so the weapon is stored here (like the
 * vanilla trident does) and synced so the client can render it. The 1.16 client only spawns vanilla
 * entity types from {@code ClientboundAddEntityPacket}, so this entity uses Forge's spawn packet instead.
 */
public class ThrownWeapon extends AbstractArrowEntity {
	private static final DataParameter<ItemStack> DATA_ITEM =
			EntityDataManager.defineId(ThrownWeapon.class, DataSerializers.ITEM_STACK);

	private ItemStack weapon = ItemStack.EMPTY;
	private float damage;
	private boolean dealtDamage;

	public ThrownWeapon(EntityType<? extends ThrownWeapon> type, World level) {
		super(type, level);
	}

	public ThrownWeapon(World level, LivingEntity owner, ItemStack stack, float damage) {
		super(ModEntities.THROWN_WEAPON.get(), owner, level);
		this.weapon = stack.copy();
		this.damage = damage;
		this.entityData.set(DATA_ITEM, weapon.copy());
		if (owner instanceof PlayerEntity) {
			this.pickup = ((PlayerEntity) owner).abilities.instabuild ? PickupStatus.CREATIVE_ONLY : PickupStatus.ALLOWED;
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
	protected EntityRayTraceResult findHitEntity(Vector3d from, Vector3d to) {
		return dealtDamage ? null : super.findHitEntity(from, to);
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result) {
		Entity target = result.getEntity();
		Entity owner = getOwner();
		float amount = damage;
		if (target instanceof LivingEntity) {
			amount += EnchantmentHelper.getDamageBonus(weapon, ((LivingEntity) target).getMobType());
		}
		DamageSource source = DamageSource.thrown(this, owner == null ? this : owner);

		dealtDamage = true;
		if (target.hurt(source, amount) && target instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) target;
			if (owner instanceof LivingEntity) {
				LivingEntity thrower = (LivingEntity) owner;
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
	public void playerTouch(PlayerEntity player) {
		Entity owner = getOwner();
		if (owner == null || owner.getUUID().equals(player.getUUID())) {
			super.playerTouch(player);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Weapon", 10)) {
			weapon = ItemStack.of(tag.getCompound("Weapon"));
		}
		damage = tag.getFloat("Damage");
		dealtDamage = tag.getBoolean("DealtDamage");
		entityData.set(DATA_ITEM, weapon.copy());
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Weapon", weapon.save(new CompoundNBT()));
		tag.putFloat("Damage", damage);
		tag.putBoolean("DealtDamage", dealtDamage);
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		// The 1.16 client only builds vanilla entity types from the vanilla spawn packet.
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		return true;
	}
}
