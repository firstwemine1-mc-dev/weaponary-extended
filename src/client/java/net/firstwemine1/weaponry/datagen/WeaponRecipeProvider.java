package net.firstwemine1.weaponry.datagen;

import java.util.concurrent.CompletableFuture;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Crafting recipes. M = tier material, S = stick. Shapes avoid every vanilla tool/weapon pattern
 * and each other (vanilla shaped recipes also match mirrored, which was checked too).
 */
public class WeaponRecipeProvider extends FabricRecipeProvider {
	public WeaponRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				for (WeaponItem item : ModItems.all()) {
					ShapedRecipeBuilder builder = shaped(RecipeCategory.COMBAT, item);
					for (String row : pattern(item)) {
						builder.pattern(row);
					}
					MaterialInput material = materialInput(this, item);
					builder.define('M', material.ingredient())
							.define('S', Items.STICK)
							.unlockedBy(material.criterionName(), material.criterion())
							.save(output);
				}
			}
		};
	}

	@Override
	public String getName() {
		return "Weaponry recipes";
	}

	private static String[] pattern(WeaponItem item) {
		return switch (item.type()) {
			case DAGGER -> new String[] {"M", "S"};
			case SABRE -> new String[] {"  M", " M ", "S  "};
			case SCIMITAR -> new String[] {" MM", " M ", "S  "};
			// Vanilla 1.21.11 spears use the old diagonal "  M / S / S  " shape, so ours is narrower.
			case SPEAR -> new String[] {" M", " S", "S "};
			case PIKE -> new String[] {" MM", " SM", "S  "};
			case LANCE -> new String[] {"  M", "MM ", "S  "};
			case HALBERD -> new String[] {"MM ", "MS ", "  S"};
			case HATCHET -> new String[] {"MM", "MS"};
			case WAR_HAMMER -> new String[] {"MMM", "MSM", " S "};
			case CLUB -> new String[] {" M", "S "};
		};
	}

	private record MaterialInput(Ingredient ingredient, String criterionName, Criterion<InventoryChangeTrigger.TriggerInstance> criterion) {
		static MaterialInput of(RecipeProvider recipes, TagKey<Item> tag, String name) {
			return new MaterialInput(recipes.tag(tag), "has_" + name, recipes.has(tag));
		}

		static MaterialInput of(RecipeProvider recipes, Item item) {
			return new MaterialInput(Ingredient.of(item), RecipeProvider.getHasName(item), recipes.has(item));
		}
	}

	private static MaterialInput materialInput(RecipeProvider recipes, WeaponItem item) {
		return switch (item.material()) {
			// The club is the cheap weapon: a wooden one is carved from a whole log.
			case WOODEN -> item.type() == WeaponType.CLUB
					? MaterialInput.of(recipes, ItemTags.LOGS, "logs")
					: MaterialInput.of(recipes, ItemTags.PLANKS, "planks");
			case STONE -> MaterialInput.of(recipes, ItemTags.STONE_TOOL_MATERIALS, "stone_tool_materials");
			case IRON -> MaterialInput.of(recipes, Items.IRON_INGOT);
			case GOLDEN -> MaterialInput.of(recipes, Items.GOLD_INGOT);
			case DIAMOND -> MaterialInput.of(recipes, Items.DIAMOND);
		};
	}
}
