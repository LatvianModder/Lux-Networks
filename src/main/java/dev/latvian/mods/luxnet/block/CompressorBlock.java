package dev.latvian.mods.luxnet.block;

import dev.latvian.mods.luxnet.item.LuxNetItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * @author LatvianModder
 */
public class CompressorBlock extends Block
{
	public CompressorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	@Deprecated
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
	{
		if (!world.isRemote() && player.getHeldItem(hand).getItem() == LuxNetItems.GRAPHENE_DUST)
		{
			player.getHeldItem(hand).shrink(1);
			ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(LuxNetItems.GRAPHENE_PLATE));
		}

		return ActionResultType.SUCCESS;
	}
}