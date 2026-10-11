package net.firstwemine1.weaponry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;

/** Forge entry point. Empty skeleton: the weapons are ported from the Fabric branches later. */
@Mod(Weaponry.MOD_ID)
public final class Weaponry {
	public static final String MOD_ID = "weaponry";
	private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public Weaponry() {
		LOGGER.info("Weaponry skeleton loaded");
	}
}
