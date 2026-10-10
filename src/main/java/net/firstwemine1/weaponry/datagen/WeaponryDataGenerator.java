package net.firstwemine1.weaponry.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WeaponryDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		generator.addProvider(WeaponModelProvider::new);
		generator.addProvider(WeaponLanguageProvider::new);
		generator.addProvider(WeaponRecipeProvider::new);
	}
}
