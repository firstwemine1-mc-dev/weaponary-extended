package net.firstwemine1.weaponry.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WeaponryDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(WeaponModelProvider::new);
		pack.addProvider(WeaponLanguageProvider::new);
		pack.addProvider(WeaponRecipeProvider::new);
		pack.addProvider(WeaponItemTagProvider::new);
	}
}
