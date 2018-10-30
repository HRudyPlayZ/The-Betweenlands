package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelLootUrn1;
import thebetweenlands.client.render.model.tile.ModelLootUrn2;
import thebetweenlands.client.render.model.tile.ModelLootUrn3;
import thebetweenlands.client.render.model.tile.ModelMudBricksAlcove;
import thebetweenlands.common.block.container.BlockMudBricksAlcove;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityMudBricksAlcove;

@SideOnly(Side.CLIENT)
public class RenderMudBricksAlcove extends TileEntitySpecialRenderer<TileEntityMudBricksAlcove> {

	private static final ModelMudBricksAlcove ALCOVE = new ModelMudBricksAlcove();
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/tiles/mud_bricks_alcove.png");

	private static final ModelLootUrn1 LOOT_URN_1 = new ModelLootUrn1();
	private static final ModelLootUrn2 LOOT_URN_2 = new ModelLootUrn2();
	private static final ModelLootUrn3 LOOT_URN_3 = new ModelLootUrn3();

	private static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_1.png");
	private static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_2.png");
	private static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/loot_urn_3.png");

	public void renderTile(TileEntityMudBricksAlcove tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		IBlockState state = tile.getWorld().getBlockState(tile.getPos());
		if (state == null || state.getBlock() != BlockRegistry.MUD_BRICKS_ALCOVE)
			return;
		EnumFacing facing = state.getValue(BlockMudBricksAlcove.FACING);
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		switch (facing) {
		case UP:
		case DOWN:
		case NORTH:
			GlStateManager.rotate(180F, 0F, 1F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case EAST:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}
		ALCOVE.render(tile, 0.0625F);
		if (tile.has_urn) {
			GlStateManager.translate(0D, 0.5625D, -0.25D);
			GlStateManager.scale(0.65D, 0.65D, 0.65D);
			GlStateManager.rotate(180F + tile.rotationOffset, 0.0F, 1F, 0F);
			switch (tile.urn_type) {
			default:
			case 0: {
				bindTexture(TEXTURE_1);
				LOOT_URN_1.render();
				break;
			}
			case 1: {
				bindTexture(TEXTURE_2);
				LOOT_URN_2.render();
				break;
			}
			case 2: {
				bindTexture(TEXTURE_3);
				LOOT_URN_3.render();
				break;
			}
			}
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void render(TileEntityMudBricksAlcove tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile == null || !tile.hasWorld()) {
			renderTileAsItem(x, y, z);
			return;
		}
		renderTile(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	private void renderTileAsItem(double x, double y, double z) {
		GlStateManager.pushMatrix();
		bindTexture(TEXTURE);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		ALCOVE.renderItem(0.0625F);
		GlStateManager.popMatrix();
	}
}