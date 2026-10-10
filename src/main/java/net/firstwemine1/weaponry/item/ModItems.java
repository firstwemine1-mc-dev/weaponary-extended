package net.firstwemine1.weaponry.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.firstwemine1.weaponry.Weaponry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class ModItems {
	private static final Map<WeaponType, Map<WeaponMaterial, WeaponItem>> WEAPONS = new EnumMap<>(WeaponType.class);
	private static final List<WeaponItem> ALL = new ArrayList<>();

	/** FabricItemGroupBuilder names the tab {@code itemGroup.<namespace>.<path>}. */
	public static final String CREATIVE_TAB_KEY = "itemGroup." + Weaponry.MOD_ID + ".weapons";

	private ModItems() {
	}

	public static void register() {
		// Items join the tab through Item.Properties.tab, so the tab is built first. The icon is
		// only asked for later, once the items exist.
		CreativeModeTab tab = FabricItemGroupBuilder.build(Weaponry.id("weapons"),
				() -> get(WeaponType.HALBERD, WeaponMaterial.IRON).getDefaultInstance())
				// Recipe advancements are filed under the tab's folder: recipes/combat/, like vanilla weapons.
				.setRecipeFolderName("combat");

		for (WeaponType type : WeaponType.values()) {
			Map<WeaponMaterial, WeaponItem> byMaterial = new EnumMap<>(WeaponMaterial.class);
			for (WeaponMaterial material : WeaponMaterial.values()) {
				WeaponItem item = new WeaponItem(type, material, new Item.Properties().tab(tab));
				Registry.register(Registry.ITEM, Weaponry.id(itemId(type, material)), item);
				byMaterial.put(material, item);
				ALL.add(item);
			}
			WEAPONS.put(type, byMaterial);
		}
	}

	public static String itemId(WeaponType type, WeaponMaterial material) {
		return material.id() + "_" + type.id();
	}

	public static WeaponItem get(WeaponType type, WeaponMaterial material) {
		return WEAPONS.get(type).get(material);
	}

	/** Every weapon, ordered by type then material. */
	public static List<WeaponItem> all() {
		return Collections.unmodifiableList(ALL);
	}
}
