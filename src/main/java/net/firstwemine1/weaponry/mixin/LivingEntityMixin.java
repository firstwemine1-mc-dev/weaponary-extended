package net.firstwemine1.weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Shield-breaker weapons (halberd, hatchet) disable shields the way axes do. */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
	@ModifyReturnValue(method = "canDisableShield", at = @At("RETURN"))
	private boolean weaponry$shieldBreaker(boolean original) {
		return original || WeaponItem.abilityOf(((LivingEntity) (Object) this).getWeaponItem()).disablesShields();
	}
}
