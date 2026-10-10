package net.firstwemine1.weaponry.mixin;

import net.firstwemine1.weaponry.item.WeaponItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sword enchantments (sharpness, smite, bane of arthropods, knockback, fire aspect, looting,
 * sweeping edge) use {@link EnchantmentCategory#WEAPON}, which only accepts {@code SwordItem}.
 * Weapons can't be swords (that would make them sweep), so this lets the category accept them too.
 * The category decides the enchanting table, the anvil and {@code /enchant} alike.
 *
 * <p>Targets the anonymous class of the {@code WEAPON} constant (its body is the 6th in the enum).
 * The identity check keeps this harmless if that ever points at another constant.
 */
@Mixin(targets = "net.minecraft.world.item.enchantment.EnchantmentCategory$6")
public abstract class WeaponEnchantmentCategoryMixin {
	@Inject(method = "canEnchant(Lnet/minecraft/world/item/Item;)Z", at = @At("HEAD"), cancellable = true)
	private void weaponry$acceptWeapons(Item item, CallbackInfoReturnable<Boolean> cir) {
		if (item instanceof WeaponItem && (Object) this == EnchantmentCategory.WEAPON) {
			cir.setReturnValue(true);
		}
	}
}
