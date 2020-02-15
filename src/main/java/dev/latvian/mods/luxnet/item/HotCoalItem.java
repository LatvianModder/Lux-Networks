package dev.latvian.mods.luxnet.item;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

/**
 * @author LatvianModder
 */
public class HotCoalItem extends Item
{
	public HotCoalItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
	{
		IFluidState fluid = entity.getEntityWorld().getFluidState(entity.getPosition());

		if (fluid.getFluid().isEquivalentTo(Fluids.WATER))
		{
			//entity.setMotion(new Vec3d(0D, 0D, 0D));
			//entity.velocityChanged = true;
			entity.setItem(new ItemStack(LuxNetItems.GRAPHITE_DUST, stack.getCount()));

			if (entity.getEntityWorld().isRemote())
			{
				for (int i = 0; i < 5 * stack.getCount(); i++)
				{
					entity.getEntityWorld().addParticle(ParticleTypes.LAVA, entity.getPosX(), entity.getPosY(), entity.getPosZ(), 0.0D, 0.0D, 0.0D);
				}

				entity.getEntityWorld().playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1F, 1F, false);
			}
		}

		return false;
	}
}