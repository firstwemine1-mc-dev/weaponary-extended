package net.firstwemine1.weaponry.datagen;

import java.util.concurrent.CompletableFuture;

import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
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
	public WeaponRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	public String getName() {
		return "Weaponry recipes";
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
					MaterialInput material = materialInput(item);
					builder.define('M', material.ingredient())
							.define('S', Items.STICK)
							.unlockedBy(material.criterionName(), material.criterion())
							.save(output);
				}
			}

			private MaterialInput of(TagKey<Item> tag, String name) {
				return new MaterialInput(tag(tag), "has_" + name, has(tag));
			}

			private MaterialInput of(Item item) {
				return new MaterialInput(Ingredient.of(item), getHasName(item), has(item));
			}

			private MaterialInput materialInput(WeaponItem item) {
				return switch (item.material()) {
					// The club is the cheap weapon: a wooden one is carved from a whole log.
					case WOODEN -> item.type() == WeaponType.CLUB
							? of(ItemTags.LOGS, "logs")
							: of(ItemTags.PLANKS, "planks");
					case STONE -> of(ItemTags.STONE_TOOL_MATERIALS, "stone_tool_materials");
					case IRON -> of(Items.IRON_INGOT);
					case GOLDEN -> of(Items.GOLD_INGOT);
					case DIAMOND -> of(Items.DIAMOND);
				};
			}
		};
	}

	private static String[] pattern(WeaponItem item) {
		return switch (item.type()) {
			case DAGGER -> new String[] {"M", "S"};
			case SABRE -> new String[] {"  M", " M ", "S  "};
			case SCIMITAR -> new String[] {" MM", " M ", "S  "};
			// Vanilla's spear (1.21.11+) took the plain diagonal {"  M", " S ", "S  "}, so ours has a wider head.
			case SPEAR -> new String[] {"  M", " SM", "S  "};
			case PIKE -> new String[] {" MM", " SM", "S  "};
			case LANCE -> new String[] {"  M", "MM ", "S  "};
			case HALBERD -> new String[] {"MMM", " S ", "S  "};
			case HATCHET -> new String[] {"MM", "MS"};
			case WAR_HAMMER -> new String[] {"MMM", "MSM", " S "};
			case CLUB -> new String[] {" M", "S "};
		};
	}

	private record MaterialInput(Ingredient ingredient, String criterionName, Criterion<InventoryChangeTrigger.TriggerInstance> criterion) {
	}
}
