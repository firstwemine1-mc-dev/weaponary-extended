package net.firstwemine1.weaponry.datagen;

import net.firstwemine1.weaponry.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class WeaponModelProvider extends FabricModelProvider {
	public WeaponModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generator) {
	}

	@Override
	public void generateItemModels(ItemModelGenerators generator) {
		// Held like a sword; texture is textures/item/<material>_<weapon>.png. Also writes items/<id>.json.
		ModItems.all().forEach(item -> generator.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM));
	}
}
