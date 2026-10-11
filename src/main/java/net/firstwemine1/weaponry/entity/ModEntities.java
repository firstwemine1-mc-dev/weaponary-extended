package net.firstwemine1.weaponry.entity;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
	public static final String THROWN_WEAPON_ID = "thrown_weapon";

	/** Same size and tracking as the vanilla trident (arrows set their own 0.13 eye height in this version). */
	public static final EntityType<ThrownWeapon> THROWN_WEAPON = Registry.register(
			Registry.ENTITY_TYPE,
			Weaponry.id(THROWN_WEAPON_ID),
			EntityType.Builder.<ThrownWeapon>of(ThrownWeapon::new, MobCategory.MISC)
					.sized(0.5f, 0.5f)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(THROWN_WEAPON_ID));

	private ModEntities() {
	}

	/** Loads the class, which registers the entity types. */
	public static void register() {
	}
}
