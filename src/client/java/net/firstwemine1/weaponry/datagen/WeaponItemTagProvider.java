package net.firstwemine1.weaponry.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponMaterial;
import net.firstwemine1.weaponry.item.WeaponType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/** Vanilla tags that make weapons enchantable and behave like melee weapons (and the hatchet like an axe). */
public class WeaponItemTagProvider extends FabricTagProvider.ItemTagProvider {
	public WeaponItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		addAll(ItemTags.SWORD_ENCHANTABLE);
		addAll(ItemTags.SHARP_WEAPON_ENCHANTABLE);
		addAll(ItemTags.WEAPON_ENCHANTABLE);
		addAll(ItemTags.FIRE_ASPECT_ENCHANTABLE);
		addAll(ItemTags.DURABILITY_ENCHANTABLE);
		addAll(ItemTags.BREAKS_DECORATED_POTS);

		// The hatchet chops wood, so it takes Efficiency, Fortune and Silk Touch like an axe.
		for (TagKey<Item> tag : List.of(ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE)) {
			var builder = getOrCreateTagBuilder(tag);
			for (WeaponMaterial material : WeaponMaterial.values()) {
				builder.add(ModItems.get(WeaponType.HATCHET, material));
			}
		}
	}

	private void addAll(TagKey<Item> tag) {
		var builder = getOrCreateTagBuilder(tag);
		ModItems.all().forEach(builder::add);
	}
}
