package dev.latvian.mods.luxnet;

import dev.latvian.mods.luxnet.block.LuxNetBlocks;
import dev.latvian.mods.luxnet.item.HotCoalItem;
import dev.latvian.mods.luxnet.item.LuxNetItems;
import dev.latvian.mods.luxnet.item.RemoteItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
@Mod("luxnet")
public class LuxNet
{
	public static LuxNet instance;
	public ItemGroup itemGroup;
	public IArmorMaterial grapheneArmorMaterial;

	public LuxNet()
	{
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerBlockEntities);
		MinecraftForge.EVENT_BUS.addListener(this::pistonEvent);

		itemGroup = new ItemGroup("luxnet")
		{
			@Override
			@OnlyIn(Dist.CLIENT)
			public ItemStack createIcon()
			{
				return new ItemStack(LuxNetItems.REMOTE);
			}
		};

		grapheneArmorMaterial = new IArmorMaterial()
		{
			@Override
			public int getDurability(EquipmentSlotType slotIn)
			{
				switch (slotIn)
				{
					case HEAD:
						return 1100;
					case CHEST:
						return 1600;
					case LEGS:
						return 1500;
					case FEET:
						return 1300;
					default:
						return 0;
				}
			}

			@Override
			public int getDamageReductionAmount(EquipmentSlotType slotIn)
			{
				return 5;
			}

			@Override
			public int getEnchantability()
			{
				return 6;
			}

			@Override
			public SoundEvent getSoundEvent()
			{
				return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
			}

			@Override
			public Ingredient getRepairMaterial()
			{
				return Ingredient.fromItems(LuxNetItems.GRAPHENE_PLATE);
			}

			@Override
			public String getName()
			{
				return "graphene";
			}

			@Override
			public float getToughness()
			{
				return 4F;
			}
		};
	}

	private void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("silicon"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("rubber"),
				new HotCoalItem(new Item.Properties().group(itemGroup)).setRegistryName("hot_coal"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("graphite_dust"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("graphene_dust"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("graphene_plate"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("simple_circuit_board"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("advanced_circuit_board"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("conductive_powder"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("luminous_powder"),
				new Item(new Item.Properties().group(itemGroup)).setRegistryName("lens"),
				new ArmorItem(grapheneArmorMaterial, EquipmentSlotType.HEAD, new Item.Properties().group(itemGroup).maxStackSize(1)).setRegistryName("graphene_helmet"),
				new ArmorItem(grapheneArmorMaterial, EquipmentSlotType.CHEST, new Item.Properties().group(itemGroup).maxStackSize(1)).setRegistryName("graphene_chestplate"),
				new ArmorItem(grapheneArmorMaterial, EquipmentSlotType.LEGS, new Item.Properties().group(itemGroup).maxStackSize(1)).setRegistryName("graphene_leggings"),
				new ArmorItem(grapheneArmorMaterial, EquipmentSlotType.FEET, new Item.Properties().group(itemGroup).maxStackSize(1)).setRegistryName("graphene_boots"),
				new RemoteItem(new Item.Properties().group(itemGroup).maxStackSize(1)).setRegistryName("remote")
		);

		event.getRegistry().registerAll(
				new BlockItem(LuxNetBlocks.GRAPHITE_DUST_BLOCK, new Item.Properties().group(itemGroup)).setRegistryName("graphite_dust_block"),
				new BlockItem(LuxNetBlocks.GRAPHENE_PLATE_BLOCK, new Item.Properties().group(itemGroup)).setRegistryName("graphene_plate_block")
		);
	}

	private void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(
				new Block(Block.Properties.create(Material.IRON, MaterialColor.GRAY).hardnessAndResistance(1F)).setRegistryName("graphite_dust_block"),
				new Block(Block.Properties.create(Material.IRON, MaterialColor.BLACK).hardnessAndResistance(1.5F)).setRegistryName("graphene_plate_block")
		);
	}

	private void registerBlockEntities(RegistryEvent.Register<TileEntityType<?>> event)
	{
	}

	private void pistonEvent(PistonEvent.Post event)
	{
		if (event.getPistonMoveType() == PistonEvent.PistonMoveType.EXTEND && event.getDirection() == Direction.DOWN && event.getWorld().getBlockState(event.getPos().down(2)).getBlock() == Blocks.SMITHING_TABLE)
		{
			BlockPos pos = event.getPos();
			Direction dir = event.getDirection();
			BlockPos offPos = pos.offset(Direction.DOWN);

			boolean createdPlate = false;

			for (ItemEntity entity : event.getWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(offPos)))
			{
				if (entity.getItem().getItem() == LuxNetItems.GRAPHENE_DUST)
				{
					createdPlate = true;
					entity.setItem(new ItemStack(LuxNetItems.GRAPHENE_PLATE, entity.getItem().getCount()));
					entity.setPosition(entity.getPosX(), entity.getPosY() + 0.4D, entity.getPosZ());
				}
			}

			if (createdPlate && event.getWorld().isRemote())
			{
				double x = offPos.getX() + 0.5D + dir.getXOffset() * 0.5D;
				double y = offPos.getY() + 0.5D + dir.getYOffset() * 0.5D;
				double z = offPos.getZ() + 0.5D + dir.getZOffset() * 0.5D;

				ItemParticleData data = new ItemParticleData(ParticleTypes.ITEM, new ItemStack(LuxNetItems.GRAPHENE_PLATE));

				for (int i = 0; i < 10; i++)
				{
					event.getWorld().addParticle(data, x, y, z, 0.0D, 0.0D, 0.0D);
				}

				event.getWorld().playEvent(1031, offPos, 0);
			}
		}
	}
}