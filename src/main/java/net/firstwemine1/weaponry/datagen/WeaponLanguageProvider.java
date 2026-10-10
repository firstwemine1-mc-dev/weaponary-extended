package net.firstwemine1.weaponry.datagen;

import net.firstwemine1.weaponry.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class WeaponLanguageProvider extends FabricLanguageProvider {
	public WeaponLanguageProvider(FabricDataGenerator generator) {
		super(generator);
	}

	@Override
	public void generateTranslations(TranslationBuilder builder) {
		builder.add(ModItems.CREATIVE_TAB_KEY, "Weaponry");
		ModItems.all().forEach(item ->
				builder.add(item, item.material().displayName() + " " + item.type().displayName()));
	}
}
