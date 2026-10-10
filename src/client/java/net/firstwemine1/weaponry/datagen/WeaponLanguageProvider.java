package net.firstwemine1.weaponry.datagen;

import java.util.concurrent.CompletableFuture;

import net.firstwemine1.weaponry.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

public class WeaponLanguageProvider extends FabricLanguageProvider {
	public WeaponLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public void generateTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
		builder.add(ModItems.CREATIVE_TAB_KEY, "Weaponry");
		ModItems.all().forEach(item ->
				builder.add(item, item.material().displayName() + " " + item.type().displayName()));
	}
}
