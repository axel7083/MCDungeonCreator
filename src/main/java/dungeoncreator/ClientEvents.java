package dungeoncreator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dungeoncreator.gui.door.TileList;
import dungeoncreator.models.Door;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import dungeoncreator.utils.TileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.minecraft.client.gui.AbstractGui.drawCenteredString;
import static net.minecraft.client.gui.AbstractGui.drawString;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event)
    {

        Minecraft mc = Minecraft.getInstance();
        // If we are in a world
        if(mc.world != null)
        {
            Cache cache = Cache.getInstance();
            if(cache.worldData == null || cache.worldData.objects == null)
                return;

            IRenderTypeBuffer.Impl renderBuffers = mc.getRenderTypeBuffers().getBufferSource();
            IVertexBuilder builder = renderBuffers.getBuffer(RenderType.LINES);
            MatrixStack matrixStack = event.getMatrixStack();

            RenderSystem.lineWidth(10.0f);
            PlayerEntity player = mc.player;
            if(player == null)
                return;

            double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

            matrixStack.push();
            matrixStack.translate(-x, -y, -z);

            renderTileBoxes(event, renderBuffers, builder, matrixStack, cache.worldData);

            InGameTile t = TileUtils.getTileWithPlayerInside(cache.worldData.objects, (int) x, (int) y, (int) z);
            if(t != null)
                renderWalkableArea(builder, matrixStack, t);

            matrixStack.pop();
            RenderSystem.disableDepthTest();
            renderBuffers.finish(RenderType.LINES);

            //Render 3D Box
            drawDoors(event,t);
        }
    }

    private static void renderTileBoxes(RenderWorldLastEvent event, IRenderTypeBuffer.Impl renderBuffers, IVertexBuilder builder, MatrixStack matrixStack, WorldData worldData) {

        worldData.objects.forEach(tileObject -> {
            if(tileObject.visible)
                WorldRenderer.drawBoundingBox(matrixStack, builder,
                        Math.min(tileObject.pos[0],tileObject.pos2[0]),
                        Math.min(tileObject.pos[1],tileObject.pos2[1])-1.5,
                        Math.min(tileObject.pos[2],tileObject.pos2[2]),
                        Math.max(tileObject.pos[0],tileObject.pos2[0])+1,
                        Math.max(tileObject.pos[1],tileObject.pos2[1])-0.5,
                        Math.max(tileObject.pos[2],tileObject.pos2[2])+1, 0f, 1.0f, 0f, 1.0f);
        });
    }

    private static void renderWalkableArea(IVertexBuilder builder, MatrixStack matrixStack, InGameTile tileObject) {

        float offset = -0.6f;
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        int minX = Math.min( tileObject.pos[0], tileObject.pos2[0]);
        int minZ = Math.min( tileObject.pos[2], tileObject.pos2[2]);

        if(tileObject.sizeX == 0 || tileObject.sizeY == 0 ||tileObject.sizeZ == 0)
            tileObject.computeSizes();

        if(tileObject.displayWalkable) {
            for(int x = 0; x< tileObject.sizeX; x++) {
                for(int z = 0; z< tileObject.sizeZ; z++) {

                    if(tileObject.regionPlane == null || tileObject.regionPlane.length == 0)
                        tileObject.generatePlane();

                    //System.out.println("LENGTH ; " + tileObject.regionPlane.length);
                    boolean minimap = tileObject.regionPlane[x][z] == 0;
                    float green = minimap?1.0f:0;
                    float red = minimap?0:1.0f;

                    // Diagonals
                    builder.pos(matrix4f, x+minX, tileObject.heightPlane[x][z]+offset, z+minZ).color(red, green, 0, 1.0f).endVertex();
                    builder.pos(matrix4f, x+minX+1, tileObject.heightPlane[x][z]+offset, z+minZ+1).color(red, green, 0, 1.0f).endVertex();

                    builder.pos(matrix4f, x+minX+1, tileObject.heightPlane[x][z]+offset, z+minZ).color(red, green, 0, 1.0f).endVertex();
                    builder.pos(matrix4f, x+minX, tileObject.heightPlane[x][z]+offset, z+minZ+1).color(red, green, 0, 1.0f).endVertex();
                }
            }
        }
    }

    // Source https://www.minecraftforgefrance.fr/topic/6412/dessin-dans-le-monde/12
    public static void drawDoors(RenderWorldLastEvent event, InGameTile t) {
        if(t == null)
            return;

        MatrixStack ms = event.getMatrixStack();
        IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        RenderSystem.disableCull();
        ms.push();

        for(Door d: t.doors) {
            drawCube(ms, buffers, new AxisAlignedBB(
                    new BlockPos(d.blockPos.getX()+d.pos[0], d.blockPos.getY()+d.pos[1], d.blockPos.getZ()+d.pos[2]))
                    .expand(d.size[0], d.size[1], d.size[2]), new Color(0, 255, 0, 100));
        }


        ms.pop();
        buffers.finish();
    }

    public static void drawCube(MatrixStack ms, IRenderTypeBuffer buffers, AxisAlignedBB aabb, Color color) {
        draw3dRectangle(ms, buffers, aabb, color, "TOP");
        draw3dRectangle(ms, buffers, aabb, color, "BOTTOM");
        draw3dRectangle(ms, buffers, aabb, color, "NORTH");
        draw3dRectangle(ms, buffers, aabb, color, "EAST");
        draw3dRectangle(ms, buffers, aabb, color, "SOUTH");
        draw3dRectangle(ms, buffers, aabb, color, "WEST");
    }

    public static void draw3dRectangle(MatrixStack ms, IRenderTypeBuffer buffers, AxisAlignedBB aabb, Color color, String side) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        double renderPosX = Minecraft.getInstance().getRenderManager().info.getProjectedView().getX();
        double renderPosY = Minecraft.getInstance().getRenderManager().info.getProjectedView().getY();
        double renderPosZ = Minecraft.getInstance().getRenderManager().info.getProjectedView().getZ();

        ms.push();
        ms.translate(aabb.minX - renderPosX, aabb.minY - renderPosY, aabb.minZ - renderPosZ);

        IVertexBuilder buffer = buffers.getBuffer(RenderType.makeType(DungeonCreator.MODID + ":rectangle_highlight", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderType.State.getBuilder().transparency(ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228515_g_")).cull(new RenderState.CullState(false)).build(false)));
        Matrix4f mat = ms.getLast().getMatrix();

        float x = (float) (aabb.maxX - aabb.minX);
        float y = (float) (aabb.maxY - aabb.minY);
        float z = (float) (aabb.maxZ - aabb.minZ);

        switch (side) {
            case "TOP":
                buffer.pos(mat, x, y, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, y, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, y, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, y, z).color(r, g, b, a).endVertex();
                break;
            case "BOTTOM":
                buffer.pos(mat, x, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, 0, z).color(r, g, b, a).endVertex();
                break;
            case "NORTH":
                buffer.pos(mat, 0, y, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, y, 0).color(r, g, b, a).endVertex();
                break;
            case "EAST":
                buffer.pos(mat, x, y, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, 0, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, y, z).color(r, g, b, a).endVertex();
                break;
            case "SOUTH":
                buffer.pos(mat, 0, y, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, 0, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, x, y, z).color(r, g, b, a).endVertex();
                break;
            case "WEST":
                buffer.pos(mat, 0, y, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, 0).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, 0, z).color(r, g, b, a).endVertex();
                buffer.pos(mat, 0, y, z).color(r, g, b, a).endVertex();
                break;
        }
        ms.pop();
    }


}
