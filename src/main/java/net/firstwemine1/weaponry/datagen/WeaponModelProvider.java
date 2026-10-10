package net.firstwemine1.weaponry.datagen;

import net.firstwemine1.weaponry.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class WeaponModelProvider extends FabricModelProvider {
	public WeaponModelProvider(FabricDataGenerator generator) {
		super(generator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators generator) {
		// Held like a sword; texture is textures/item/<material>_<weapon>.png
		ModItems.all().forEach(item -> generator.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM));
	}
}
