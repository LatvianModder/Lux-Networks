package dev.latvian.mods.luxnet.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.latvian.mods.luxnet.block.entity.LaserEmitterEntity;
import dev.latvian.mods.luxnet.block.entity.LaserPathNode;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;

/**
 * @author LatvianModder
 */
public class LaserEmitterRenderer extends TileEntityRenderer<LaserEmitterEntity>
{
	public LaserEmitterRenderer(TileEntityRendererDispatcher dispatcher)
	{
		super(dispatcher);
	}

	@Override
	public void render(LaserEmitterEntity laserEmitter, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer typeBuffer, int light1, int light2)
	{
		if (!laserEmitter.getBlockState().get(BlockStateProperties.POWERED))
		{
			return;
		}

		int x = 0;
		int y = 0;
		int z = 0;

		IVertexBuilder builder = typeBuffer.getBuffer(RenderType.getLines());
		matrixStack.push();
		matrixStack.translate(0.5D, 0.5D, 0.5D);

		for (LaserPathNode node : laserEmitter.path)
		{
			Matrix4f m = matrixStack.getLast().getMatrix();

			int px = x;
			int py = y;
			int pz = z;
			x += node.direction.getXOffset() * node.length;
			y += node.direction.getYOffset() * node.length;
			z += node.direction.getZOffset() * node.length;

			builder.pos(m, px, py, pz).color(199, 3, 31, 180).endVertex();
			builder.pos(m, x, y, z).color(199, 3, 31, 180).endVertex();
			//world.addParticle(new RedstoneParticleData(0.78F, 0.01F, 0.12F, 1F), x, y, z, 0.0D, 0.0D, 0.0D);
		}

		matrixStack.pop();
	}

	@Override
	public boolean isGlobalRenderer(LaserEmitterEntity laserEmitter)
	{
		return true;
	}
}