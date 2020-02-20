package dev.latvian.mods.luxnet.block;

import dev.latvian.mods.luxnet.block.entity.LaserPathTracer;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface LaserDevice
{
	default boolean laserPassesThrough(LaserPathTracer tracer, BlockState state, BlockPos pos)
	{
		return false;
	}

	@Nullable
	default Direction redirectLaser(LaserPathTracer tracer, BlockState state, BlockPos pos, Direction from)
	{
		return null;
	}

	default void updateLaserPaths(LaserPathTracer tracer, BlockState state, BlockPos pos)
	{
	}
}