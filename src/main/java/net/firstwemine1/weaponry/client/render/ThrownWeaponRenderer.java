package net.firstwemine1.weaponry.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.firstwemine1.weaponry.entity.ThrownWeapon;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Draws the thrown weapon's own item model, tip first along its flight path. Hatchets spin end over
 * end until they stick.
 */
public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeapon> {
	/** Weapon textures point up and to the right, like vanilla items; turn that onto +X. */
	private static final float TEXTURE_TILT = -45.0f;
	private static final float SPIN_DEGREES_PER_TICK = 40.0f;
	private static final float HATCHET_SCALE = 0.75f;

	private final ItemRenderer itemRenderer;

	public ThrownWeaponRenderer(EntityRendererManager dispatcher) {
		super(dispatcher);
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(ThrownWeapon entity, float yaw, float partialTick, MatrixStack poseStack, IRenderTypeBuffer buffers, int light) {
		ItemStack stack = entity.getItem();
		if (stack.isEmpty()) {
			return;
		}
		boolean hatchet = WeaponItem.is(stack, WeaponType.HATCHET);

		poseStack.pushPose();
		// Arrow rotations: +X rotated by these points along the velocity.
		poseStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTick, entity.yRotO, entity.yRot) - 90.0f));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTick, entity.xRotO, entity.xRot)));
		if (hatchet) {
			if (!entity.isStuck()) {
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(-(entity.tickCount + partialTick) * SPIN_DEGREES_PER_TICK));
			}
			poseStack.scale(HATCHET_SCALE, HATCHET_SCALE, HATCHET_SCALE);
		}
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(TEXTURE_TILT));
		itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY,
				poseStack, buffers);
		poseStack.popPose();
		super.render(entity, yaw, partialTick, poseStack, buffers, light);
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownWeapon entity) {
		return PlayerContainer.BLOCK_ATLAS;
	}
}
