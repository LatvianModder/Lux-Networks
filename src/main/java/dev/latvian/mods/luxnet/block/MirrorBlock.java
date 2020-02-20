package dev.latvian.mods.luxnet.block;

import dev.latvian.mods.luxnet.block.entity.LaserPathNode;
import dev.latvian.mods.luxnet.block.entity.LaserPathTracer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MirrorBlock extends Block implements LaserDevice
{
	public static final EnumProperty<MirrorDirection> DIRECTION = EnumProperty.create("direction", MirrorDirection.class);

	public MirrorBlock(Properties properties)
	{
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(DIRECTION, MirrorDirection.NORTH_WEST_SOUTH_EAST).with(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	@Deprecated
	public boolean isTransparent(BlockState state)
	{
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@Deprecated
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos)
	{
		return 1F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos)
	{
		return true;
	}

	@Override
	@Deprecated
	public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	@Override
	@Deprecated
	public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
	{
		return false;
	}

	@Override
	@Deprecated
	public boolean canEntitySpawn(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> entityType)
	{
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		BlockState s = getDefaultState().with(BlockStateProperties.WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
		Direction face = context.getFace();

		if (face == Direction.DOWN || face == Direction.UP)
		{
			int i = MathHelper.floor(context.getPlacementYaw() / 90F) & 3;
			return s.with(DIRECTION, i == 0 || i == 2 ? MirrorDirection.NORTH_EAST_SOUTH_WEST : MirrorDirection.NORTH_WEST_SOUTH_EAST);
		}

		double hy = context.getHitVec().y - context.getPos().getY();

		for (MirrorDirection direction : MirrorDirection.VALUES)
		{
			if (face == (hy > 0.5D ? direction.directionA2 : direction.directionB2))
			{
				return s.with(DIRECTION, direction);
			}
		}

		return s;
	}

	@Override
	@Deprecated
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
	{
		if (state.get(BlockStateProperties.WATERLOGGED))
		{
			world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return state;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(DIRECTION, BlockStateProperties.WATERLOGGED);
	}

	@Override
	@Deprecated
	public IFluidState getFluidState(BlockState state)
	{
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	@Deprecated
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
	{
		return false;
	}

	@Nullable
	@Override
	public Direction redirectLaser(LaserPathTracer tracer, BlockState state, BlockPos pos, Direction from)
	{
		return state.get(DIRECTION).redirect(from);
	}

	@Override
	public boolean laserPassesThrough(LaserPathTracer tracer, BlockState state, BlockPos pos)
	{
		return true;
	}

	@Override
	@Deprecated
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving)
	{
		trace(state, world, pos);
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		trace(state, world, pos);
	}

	private void trace(BlockState state, World world, BlockPos pos)
	{
		for (Direction direction : state.get(DIRECTION).directions)
		{
			LaserPathNode node = new LaserPathNode();
			node.direction = direction;
			LaserPathTracer tracer = new LaserPathTracer(world, pos);
			node.trace(tracer, tracer.startPosition);
			BlockPos pos1 = node.last(tracer.startPosition);
			BlockState state1 = tracer.world.getBlockState(pos1);

			if (state1.getBlock() instanceof LaserDevice)
			{
				((LaserDevice) state1.getBlock()).updateLaserPaths(tracer, state1, pos1);
			}
		}
	}
}