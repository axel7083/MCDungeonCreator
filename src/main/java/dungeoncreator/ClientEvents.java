package dungeoncreator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dungeoncreator.models.TileObject;
import dungeoncreator.utils.TileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event)
    {
        // If we are in a world
        if(Minecraft.getInstance().world != null)
        {
            GroupObject groupObject = GroupObject.getInstance();
            if(groupObject == null || groupObject.objects == null)
                return;

            IRenderTypeBuffer.Impl renderBuffers = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            IVertexBuilder builder = renderBuffers.getBuffer(RenderType.LINES);
            MatrixStack matrixStack = event.getMatrixStack();

            RenderSystem.lineWidth(10.0f);
            PlayerEntity player = Minecraft.getInstance().player;
            if(player == null)
                return;

            double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

            matrixStack.push();
            matrixStack.translate(-x, -y, -z);

            renderTileBoxes(event, renderBuffers, builder, matrixStack, groupObject);

            TileObject t = TileUtils.getTileWithPlayerInside(groupObject.objects, (int) x, (int) y, (int) z);
            if(t != null)
                renderWalkableArea(builder, matrixStack, t);

            matrixStack.pop();
            RenderSystem.disableDepthTest();
            renderBuffers.finish(RenderType.LINES);
        }
    }

    private static void renderTileBoxes(RenderWorldLastEvent event, IRenderTypeBuffer.Impl renderBuffers, IVertexBuilder builder, MatrixStack matrixStack, GroupObject groupObject) {

        groupObject.objects.forEach(tileObject -> {
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

    private static void renderWalkableArea(IVertexBuilder builder, MatrixStack matrixStack, TileObject tileObject) {

        float offset = -0.6f;
        Matrix4f matrix4f = matrixStack.getLast().getMatrix();

        int minX = Math.min( tileObject.pos[0], tileObject.pos2[0]);
        int minZ = Math.min( tileObject.pos[2], tileObject.pos2[2]);

        if(tileObject.displayWalkable) {
            for(int x = 0; x< tileObject.sizeX; x++) {
                for(int z = 0; z< tileObject.sizeZ; z++) {

                    if(tileObject.regionPlane == null)
                        tileObject.generatePlane();

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



}
