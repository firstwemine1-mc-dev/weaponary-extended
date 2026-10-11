package net.firstwemine1.weaponry.mixin;

import net.firstwemine1.weaponry.ability.SabreAbility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The sabre parry (shield breaking is in {@code PlayerMixin.blockUsingShield} in this version). */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
	/** The sabre parry, run where later Fabric API versions fire {@code ALLOW_DAMAGE}. */
	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void weaponry$parry(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (!self.level.isClientSide() && !SabreAbility.allowDamage(self, source, amount)) {
			cir.setReturnValue(false);
		}
	}
}
