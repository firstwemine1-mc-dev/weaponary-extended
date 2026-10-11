package net.firstwemine1.weaponry;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/** NeoForge entry point. Empty skeleton: the weapons are ported from the Fabric branches later. */
@Mod(Weaponry.MOD_ID)
public final class Weaponry {
	public static final String MOD_ID = "weaponry";
	private static final Logger LOGGER = LogUtils.getLogger();

	public Weaponry(IEventBus modEventBus) {
		LOGGER.info("Weaponry skeleton loaded");
	}
}
