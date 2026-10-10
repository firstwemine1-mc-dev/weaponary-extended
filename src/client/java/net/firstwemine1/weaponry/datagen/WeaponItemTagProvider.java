package net.firstwemine1.weaponry.datagen;

import java.util.concurrent.CompletableFuture;

import net.firstwemine1.weaponry.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/** Vanilla tags that make weapons enchantable and behave like melee weapons. */
public class WeaponItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
	public WeaponItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		// Sword enchantments minus Sweeping Edge (#enchantable/sweeping): weapons don't sweep.
		addAll(ItemTags.MELEE_WEAPON_ENCHANTABLE);
		addAll(ItemTags.SHARP_WEAPON_ENCHANTABLE);
		addAll(ItemTags.WEAPON_ENCHANTABLE);
		addAll(ItemTags.FIRE_ASPECT_ENCHANTABLE);
		addAll(ItemTags.DURABILITY_ENCHANTABLE);
		addAll(ItemTags.BREAKS_DECORATED_POTS);
	}

	private void addAll(TagKey<Item> tag) {
		var builder = valueLookupBuilder(tag);
		ModItems.all().forEach(builder::add);
	}
}
