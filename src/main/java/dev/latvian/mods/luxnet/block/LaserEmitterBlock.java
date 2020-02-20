package dev.latvian.mods.luxnet.block;

import dev.latvian.mods.luxnet.block.entity.LaserEmitterEntity;
import dev.latvian.mods.luxnet.block.entity.LaserPathTracer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class LaserEmitterBlock extends DirectionalBlock implements LaserDevice
{
	public LaserEmitterBlock(Properties properties)
	{
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.SOUTH).with(BlockStateProperties.POWERED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		LaserEmitterEntity laserEmitter = new LaserEmitterEntity();
		laserEmitter.path.direction = state.get(FACING);
		return laserEmitter;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, BlockStateProperties.POWERED);
	}

	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		return getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		boolean powered = world.isBlockPowered(pos);

		if (powered != state.get(BlockStateProperties.POWERED))
		{
			TileEntity entity = world.getTileEntity(pos);

			if (entity instanceof LaserEmitterEntity)
			{
				((LaserEmitterEntity) entity).redstoneChanged(powered);
			}

			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, powered), 3);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random)
	{
		if (state.get(BlockStateProperties.POWERED))
		{
			int cx = pos.getX();
			int cy = pos.getY();
			int cz = pos.getZ();
			Direction d = state.get(FACING);
			double x = cx + 0.5D + d.getXOffset() * 0.6D;
			double y = cy + 0.5D + d.getYOffset() * 0.6D;
			double z = cz + 0.5D + d.getZOffset() * 0.6D;

			for (int i = 0; i < 3; i++)
			{
				world.addParticle(new RedstoneParticleData(0.78F, 0.01F, 0.12F, 1F), x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity livingEntity, ItemStack stack)
	{
		super.onBlockPlacedBy(world, pos, state, livingEntity, stack);

		if (livingEntity != null)
		{
			TileEntity entity = world.getTileEntity(pos);

			if (entity instanceof LaserEmitterEntity)
			{
				((LaserEmitterEntity) entity).owner = livingEntity.getUniqueID();
				entity.markDirty();
			}
		}
	}

	@Override
	@Deprecated
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		TileEntity entity = world.getTileEntity(pos);

		if (entity instanceof LaserEmitterEntity)
		{
			((LaserEmitterEntity) entity).tracePath(state, new LaserPathTracer(world, pos));
		}
	}

	@Override
	public void updateLaserPaths(LaserPathTracer tracer, BlockState state, BlockPos pos)
	{
		TileEntity entity = tracer.world.getTileEntity(pos);

		if (entity instanceof LaserEmitterEntity)
		{
			((LaserEmitterEntity) entity).tracePath(state, new LaserPathTracer(tracer.world, pos));
		}
	}
}