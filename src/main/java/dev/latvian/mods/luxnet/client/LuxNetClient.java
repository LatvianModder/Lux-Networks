package dev.latvian.mods.luxnet.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.latvian.mods.luxnet.block.LuxNetBlocks;
import dev.latvian.mods.luxnet.block.entity.LuxNetBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author LatvianModder
 */
public class LuxNetClient
{
	public LuxNetClient()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		MinecraftForge.EVENT_BUS.addListener(this::renderHighlight);
	}

	private void setup(FMLClientSetupEvent event)
	{
		//RenderTypeLookup.setRenderLayer(LuxNetBlocks.MIRROR, RenderType.getCutout());
		ClientRegistry.bindTileEntityRenderer(LuxNetBlockEntities.LASER_EMITTER, LaserEmitterRenderer::new);
	}

	private void renderHighlight(DrawHighlightEvent.HighlightBlock event)
	{
		if (!renderHighlightHand(event, Hand.MAIN_HAND))
		{
			renderHighlightHand(event, Hand.OFF_HAND);
		}
	}

	private boolean renderHighlightHand(DrawHighlightEvent.HighlightBlock event, Hand hand)
	{
		if (Minecraft.getInstance().player.getHeldItem(hand).getItem() == LuxNetBlocks.MIRROR.asItem())
		{
			BlockPos pos = event.getTarget().getPos().offset(event.getTarget().getFace());

			MatrixStack matrixStack = event.getMatrix();
			IRenderTypeBuffer buffers = event.getBuffers();

			double camX = event.getInfo().getProjectedView().x;
			double camY = event.getInfo().getProjectedView().y;
			double camZ = event.getInfo().getProjectedView().z;

			matrixStack.push();
			matrixStack.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

			BlockState renderState = LuxNetBlocks.MIRROR.getStateForPlacement(new BlockItemUseContext(new ItemUseContext(Minecraft.getInstance().player, Hand.MAIN_HAND, event.getTarget())));

			if (renderState != null)
			{
				Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(renderState, matrixStack, buffers, 15728880, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
			}

			matrixStack.pop();
			return true;
		}

		return false;
	}
}