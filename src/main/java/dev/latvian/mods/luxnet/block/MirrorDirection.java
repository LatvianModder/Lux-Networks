package dev.latvian.mods.luxnet.block;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public enum MirrorDirection implements IStringSerializable
{
	DOWN_EAST_UP_WEST("down_east_up_west", Direction.DOWN, Direction.EAST),
	DOWN_WEST_UP_EAST("down_west_up_east", Direction.DOWN, Direction.WEST),
	DOWN_SOUTH_UP_NORTH("down_south_up_north", Direction.DOWN, Direction.SOUTH),
	DOWN_NORTH_UP_SOUTH("down_north_up_south", Direction.DOWN, Direction.NORTH),
	NORTH_EAST_SOUTH_WEST("north_east_south_west", Direction.NORTH, Direction.EAST),
	NORTH_WEST_SOUTH_EAST("north_west_south_east", Direction.NORTH, Direction.WEST);

	public static final MirrorDirection[] VALUES = values();

	private final String name;
	public final Direction directionA1;
	public final Direction directionA2;
	public final Direction directionB1;
	public final Direction directionB2;
	public final Direction[] directions;

	MirrorDirection(String n, Direction da1, Direction da2)
	{
		name = n;
		directionA1 = da1;
		directionA2 = da2;
		directionB1 = directionA1.getOpposite();
		directionB2 = directionA2.getOpposite();
		directions = new Direction[] {directionA1, directionA2, directionB1, directionB2};
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Nullable
	public Direction redirect(Direction from)
	{
		if (from == directionA1)
		{
			return directionA2;
		}
		else if (from == directionA2)
		{
			return directionA1;
		}
		else if (from == directionB1)
		{
			return directionB2;
		}
		else if (from == directionB2)
		{
			return directionB1;
		}

		return null;
	}
}