package net.firstwemine1.weaponry.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.firstwemine1.weaponry.entity.ThrownWeapon;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws the thrown weapon's own item model, tip first along its flight path. Hatchets spin end over
 * end until they stick.
 */
public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeapon, ThrownWeaponRenderer.State> {
	/** Weapon textures point up and to the right, like vanilla items; turn that onto +X. */
	private static final float TEXTURE_TILT = -45.0f;
	private static final float SPIN_DEGREES_PER_TICK = 40.0f;
	private static final float HATCHET_SCALE = 0.75f;

	private final ItemModelResolver itemModelResolver;

	public ThrownWeaponRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
	}

	public static class State extends EntityRenderState {
		public final ItemStackRenderState item = new ItemStackRenderState();
		public float yRot;
		public float xRot;
		public boolean hatchet;
		public boolean stuck;
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(ThrownWeapon entity, State state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		ItemStack stack = entity.getItem();
		itemModelResolver.updateForNonLiving(state.item, stack, ItemDisplayContext.NONE, entity);
		state.yRot = entity.getYRot(partialTick);
		state.xRot = entity.getXRot(partialTick);
		state.hatchet = WeaponItem.is(stack, WeaponType.HATCHET);
		state.stuck = entity.isStuck();
	}

	@Override
	public void render(State state, PoseStack poseStack, MultiBufferSource buffers, int light) {
		if (state.item.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		// Arrow rotations: +X rotated by these points along the velocity.
		poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f));
		poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
		if (state.hatchet) {
			if (!state.stuck) {
				poseStack.mulPose(Axis.ZP.rotationDegrees(-state.ageInTicks * SPIN_DEGREES_PER_TICK));
			}
			poseStack.scale(HATCHET_SCALE, HATCHET_SCALE, HATCHET_SCALE);
		}
		poseStack.mulPose(Axis.ZP.rotationDegrees(TEXTURE_TILT));
		state.item.render(poseStack, buffers, light, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(state, poseStack, buffers, light);
	}
}
