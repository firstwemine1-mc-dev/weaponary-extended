package net.firstwemine1.weaponry;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

/**
 * {@code config/weaponry.json}. Created with defaults on first launch; edit it and restart.
 */
public class WeaponryConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(Weaponry.MOD_ID + ".json");

	/**
	 * Testing aid: on every server start, place (or refill) a chest next to world spawn holding one
	 * of every weapon. Defaults to on in the dev environment (runClient/runServer), off otherwise.
	 */
	public boolean testChest = FabricLoader.getInstance().isDevelopmentEnvironment();

	public static WeaponryConfig load() {
		WeaponryConfig config = null;
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH)) {
				config = GSON.fromJson(reader, WeaponryConfig.class);
			} catch (IOException | JsonParseException e) {
				Weaponry.LOGGER.error("Could not read {}, using defaults", PATH, e);
			}
		}
		if (config == null) {
			config = new WeaponryConfig();
		}
		config.save();
		return config;
	}

	private void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			Weaponry.LOGGER.error("Could not write {}", PATH, e);
		}
	}
}
