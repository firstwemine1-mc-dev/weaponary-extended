package net.firstwemine1.weaponry.testing;

import java.util.List;

import net.firstwemine1.weaponry.Weaponry;
import net.firstwemine1.weaponry.item.ModItems;
import net.firstwemine1.weaponry.item.WeaponItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Testing aid (config {@code testChest}): a double chest two blocks east of world spawn holding one
 * of every weapon. Refilled on every server start; anything else put in it is cleared.
 * Weapons have durability, so they stack to 1: one of each (50) fits in a double chest (54).
 */
public final class TestChest {
	private static final String NAME = "Weaponry Test Chest";
	private static final int OFFSET_EAST = 2;

	private TestChest() {
	}

	public static void placeOrRefill(MinecraftServer server) {
		ServerLevel level = server.overworld();
		BlockPos spawn = level.getSharedSpawnPos();
		int x = spawn.getX() + OFFSET_EAST;
		int z = spawn.getZ();

		BlockPos pos = findExisting(level, x, z);
		if (pos == null) {
			pos = place(level, x, z);
		}

		BlockState state = level.getBlockState(pos);
		Container container = ChestBlock.getContainer((ChestBlock) state.getBlock(), state, level, pos, true);
		container.clearContent();
		List<WeaponItem> weapons = ModItems.all();
		for (int slot = 0; slot < weapons.size() && slot < container.getContainerSize(); slot++) {
			container.setItem(slot, new ItemStack(weapons.get(slot)));
		}
		container.setChanged();
		Weaponry.LOGGER.info("Test chest filled with {} weapons at {} {} {}", weapons.size(), pos.getX(), pos.getY(), pos.getZ());
	}

	/** Our chest from an earlier launch, so restarts refill it instead of stacking new ones. */
	private static BlockPos findExisting(ServerLevel level, int x, int z) {
		for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
			BlockPos pos = new BlockPos(x, y, z);
			if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest
					&& chest.getCustomName() != null && NAME.equals(chest.getCustomName().getString())) {
				return pos;
			}
		}
		return null;
	}

	private static BlockPos place(ServerLevel level, int x, int z) {
		// Front faces west, toward spawn. The right half sits on the side ChestBlock expects.
		BlockState left = Blocks.CHEST.defaultBlockState()
				.setValue(ChestBlock.FACING, Direction.WEST)
				.setValue(ChestBlock.TYPE, ChestType.LEFT);
		Direction toRight = ChestBlock.getConnectedDirection(left);
		BlockState right = left.setValue(ChestBlock.TYPE, ChestType.RIGHT);

		int y = Math.max(
				level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z),
				level.getHeight(Heightmap.Types.WORLD_SURFACE, x + toRight.getStepX(), z + toRight.getStepZ()));
		BlockPos leftPos = new BlockPos(x, y, z);
		BlockPos rightPos = leftPos.relative(toRight);
		level.setBlock(leftPos, left, Block.UPDATE_ALL);
		level.setBlock(rightPos, right, Block.UPDATE_ALL);

		ItemStack named = new ItemStack(Items.CHEST);
		named.set(DataComponents.CUSTOM_NAME, Component.literal(NAME));
		for (BlockPos pos : List.of(leftPos, rightPos)) {
			if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
				chest.applyComponentsFromItemStack(named);
				chest.setChanged();
			}
		}
		return leftPos;
	}
}
