package net.firstwemine1.weaponry.combat;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Server-side velocity of players and whatever they ride. Players (and the horses they steer) are
 * moved by client packets, so their server-side {@code deltaMovement} is unreliable; this samples
 * their position once per tick instead. Other entities move on the server, so their last tick's
 * movement is exact.
 */
public final class Motion {
	private static final Map<Entity, Sample> SAMPLES = new WeakHashMap<>();

	private record Sample(Vec3 lastPos, Vec3 velocity, long tick) {
	}

	private Motion() {
	}

	public static void tick(MinecraftServer server) {
		long tick = server.getTickCount();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			sample(player, tick);
			Entity mount = player.getRootVehicle();
			if (mount != player) {
				sample(mount, tick);
			}
		}
	}

	private static void sample(Entity entity, long tick) {
		Vec3 pos = entity.position();
		Sample previous = SAMPLES.get(entity);
		Vec3 velocity = Vec3.ZERO;
		if (previous != null && previous.tick == tick - 1) {
			// Smooth over two ticks: move packets don't arrive exactly once per tick.
			velocity = previous.velocity.scale(0.5).add(pos.subtract(previous.lastPos).scale(0.5));
		}
		SAMPLES.put(entity, new Sample(pos, velocity, tick));
	}

	/** Blocks per tick. */
	public static Vec3 velocity(Entity entity) {
		Sample sample = SAMPLES.get(entity);
		if (sample != null && entity.getServer() != null && entity.getServer().getTickCount() - sample.tick <= 1) {
			return sample.velocity;
		}
		return new Vec3(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo);
	}

	public static double horizontalSpeed(Entity entity) {
		return velocity(entity).horizontalDistance();
	}

	/** How fast {@code mover} (or what it rides) is closing in on {@code toward}, in blocks per tick. */
	public static double closingSpeed(Entity mover, Entity toward) {
		Entity body = mover.getRootVehicle();
		Vec3 direction = toward.position().subtract(body.position()).multiply(1, 0, 1);
		if (direction.lengthSqr() < 1.0e-6) {
			return 0;
		}
		return velocity(body).multiply(1, 0, 1).dot(direction.normalize());
	}
}
