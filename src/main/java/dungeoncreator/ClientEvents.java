package dungeoncreator;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event)
    {
        if(Minecraft.getInstance().world != null)
        {
            GroupObject groupObject = GroupObject.getInstance();
            if(groupObject == null || groupObject.objects == null)
                return;

            //System.out.println("Rendering...");
            RenderSystem.lineWidth(10.0f);
            IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
            if(buffer == null)
                return;
            IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);
            MatrixStack matrixStack = event.getMatrixStack();
            PlayerEntity player = Minecraft.getInstance().player;
            double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * event.getPartialTicks();
            double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * event.getPartialTicks();
            double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * event.getPartialTicks();

            matrixStack.push();
            matrixStack.translate(-x, -y, -z);

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
            matrixStack.pop();
            RenderSystem.disableDepthTest();
            buffer.finish(RenderType.LINES);
        }
    }

}
