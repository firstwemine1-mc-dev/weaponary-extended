package net.firstwemine1.weaponry.entity;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.firstwemine1.weaponry.Weaponry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

/**
 * Spawns a modded entity on the client. The 1.16 client builds entities from
 * {@code ClientboundAddEntityPacket} with a hard-coded list of vanilla types, so this sends the same
 * fields over a custom channel; {@code WeaponryClient} reads them with {@link #read} and does what
 * vanilla does for an arrow. Entity data (the thrown weapon's item) follows in the usual data packet.
 */
public final class SpawnPacket {
	public static final ResourceLocation ID = Weaponry.id("spawn_entity");

	private SpawnPacket() {
	}

	public static Packet<?> create(Entity entity) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeVarInt(Registry.ENTITY_TYPE.getId(entity.getType()));
		buf.writeVarInt(entity.getId());
		buf.writeUUID(entity.getUUID());
		buf.writeDouble(entity.getX());
		buf.writeDouble(entity.getY());
		buf.writeDouble(entity.getZ());
		buf.writeFloat(entity.xRot);
		buf.writeFloat(entity.yRot);
		Vec3 motion = entity.getDeltaMovement();
		buf.writeDouble(motion.x);
		buf.writeDouble(motion.y);
		buf.writeDouble(motion.z);
		Entity owner = entity instanceof Projectile ? ((Projectile) entity).getOwner() : null;
		buf.writeVarInt(owner == null ? 0 : owner.getId());
		return ServerPlayNetworking.createS2CPacket(ID, buf);
	}

	/** The decoded fields; built on the network thread, applied on the client thread. */
	public static final class Data {
		public final EntityType<?> type;
		public final int id;
		public final UUID uuid;
		public final double x;
		public final double y;
		public final double z;
		public final float xRot;
		public final float yRot;
		public final Vec3 motion;
		public final int ownerId;

		private Data(FriendlyByteBuf buf) {
			this.type = Registry.ENTITY_TYPE.byId(buf.readVarInt());
			this.id = buf.readVarInt();
			this.uuid = buf.readUUID();
			this.x = buf.readDouble();
			this.y = buf.readDouble();
			this.z = buf.readDouble();
			this.xRot = buf.readFloat();
			this.yRot = buf.readFloat();
			this.motion = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			this.ownerId = buf.readVarInt();
		}
	}

	public static Data read(FriendlyByteBuf buf) {
		return new Data(buf);
	}
}
