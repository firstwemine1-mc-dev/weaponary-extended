package net.firstwemine1.weaponry.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.firstwemine1.weaponry.entity.ThrownWeapon;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.firstwemine1.weaponry.item.WeaponType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

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

	public ThrownWeaponRenderer(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(ThrownWeapon entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
		ItemStack stack = entity.getItem();
		if (stack.isEmpty()) {
			return;
		}
		boolean hatchet = WeaponItem.is(stack, WeaponType.HATCHET);

		poseStack.pushPose();
		// Arrow rotations: +X rotated by these points along the velocity.
		poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.yRot) - 90.0f));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.xRot)));
		if (hatchet) {
			if (!entity.isStuck()) {
				poseStack.mulPose(Vector3f.ZP.rotationDegrees(-(entity.tickCount + partialTick) * SPIN_DEGREES_PER_TICK));
			}
			poseStack.scale(HATCHET_SCALE, HATCHET_SCALE, HATCHET_SCALE);
		}
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(TEXTURE_TILT));
		itemRenderer.renderStatic(stack, ItemTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY,
				poseStack, buffers);
		poseStack.popPose();
		super.render(entity, yaw, partialTick, poseStack, buffers, light);
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownWeapon entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
