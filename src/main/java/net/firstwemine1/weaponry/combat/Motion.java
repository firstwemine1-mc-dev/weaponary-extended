package net.firstwemine1.weaponry.combat;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Server-side velocity of players and whatever they ride. Players (and the horses they steer) are
 * moved by client packets, so their server-side {@code deltaMovement} is unreliable; this samples
 * their position once per tick instead. Other entities move on the server, so their last tick's
 * movement is exact.
 */
public final class Motion {
	private static final Map<Entity, Sample> SAMPLES = new WeakHashMap<>();

	private static final class Sample {
		final Vector3d lastPos;
		final Vector3d velocity;
		final long tick;

		Sample(Vector3d lastPos, Vector3d velocity, long tick) {
			this.lastPos = lastPos;
			this.velocity = velocity;
			this.tick = tick;
		}
	}

	private Motion() {
	}

	public static void tick(MinecraftServer server) {
		long tick = server.getTickCount();
		for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
			sample(player, tick);
			Entity mount = player.getRootVehicle();
			if (mount != player) {
				sample(mount, tick);
			}
		}
	}

	private static void sample(Entity entity, long tick) {
		Vector3d pos = entity.position();
		Sample previous = SAMPLES.get(entity);
		Vector3d velocity = Vector3d.ZERO;
		if (previous != null && previous.tick == tick - 1) {
			// Smooth over two ticks: move packets don't arrive exactly once per tick.
			velocity = previous.velocity.scale(0.5).add(pos.subtract(previous.lastPos).scale(0.5));
		}
		SAMPLES.put(entity, new Sample(pos, velocity, tick));
	}

	/** Blocks per tick. */
	public static Vector3d velocity(Entity entity) {
		Sample sample = SAMPLES.get(entity);
		if (sample != null && entity.getServer() != null && entity.getServer().getTickCount() - sample.tick <= 1) {
			return sample.velocity;
		}
		return new Vector3d(entity.getX() - entity.xo, entity.getY() - entity.yo, entity.getZ() - entity.zo);
	}

	public static double horizontalSpeed(Entity entity) {
		Vector3d v = velocity(entity);
		return Math.sqrt(v.x * v.x + v.z * v.z);
	}

	/** How fast {@code mover} (or what it rides) is closing in on {@code toward}, in blocks per tick. */
	public static double closingSpeed(Entity mover, Entity toward) {
		Entity body = mover.getRootVehicle();
		Vector3d direction = toward.position().subtract(body.position()).multiply(1, 0, 1);
		if (direction.lengthSqr() < 1.0e-6) {
			return 0;
		}
		return velocity(body).multiply(1, 0, 1).dot(direction.normalize());
	}
}
