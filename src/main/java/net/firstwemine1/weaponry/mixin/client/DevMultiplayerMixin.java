package net.firstwemine1.weaponry.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Dev environment only: lets {@code runClient} join servers and chat. On 1.16.5 both are gated on
 * Mojang's social-interactions service, which turns the offline dev account away ("Multiplayer is
 * disabled. Please check your Microsoft account settings"). Normal installs are unaffected.
 */
@Mixin(Minecraft.class)
abstract class DevMultiplayerMixin {
	@ModifyReturnValue(method = "allowsMultiplayer", at = @At("RETURN"))
	private boolean weaponry$devMultiplayer(boolean allowed) {
		return allowed || FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@ModifyReturnValue(method = "allowsChat", at = @At("RETURN"))
	private boolean weaponry$devChat(boolean allowed) {
		return allowed || FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
