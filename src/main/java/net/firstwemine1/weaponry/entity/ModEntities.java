package net.firstwemine1.weaponry.entity;

import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModEntities {
	public static final String THROWN_WEAPON_ID = "thrown_weapon";

	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(ForgeRegistries.ENTITIES, Weaponry.MOD_ID);

	/** Same size and tracking as the vanilla trident (arrows set their own 0.13 eye height in this version). */
	public static final RegistryObject<EntityType<ThrownWeapon>> THROWN_WEAPON = ENTITY_TYPES.register(THROWN_WEAPON_ID,
			() -> EntityType.Builder.<ThrownWeapon>of(ThrownWeapon::new, EntityClassification.MISC)
					.sized(0.5f, 0.5f)
					.clientTrackingRange(4)
					.updateInterval(20)
					.build(THROWN_WEAPON_ID));

	private ModEntities() {
	}

	public static void register(IEventBus modBus) {
		ENTITY_TYPES.register(modBus);
	}
}
