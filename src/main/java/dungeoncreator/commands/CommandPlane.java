package dungeoncreator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dungeoncreator.GroupObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class CommandPlane {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandTile
                = Commands.literal("plane")
                .requires((commandSource) -> commandSource.hasPermissionLevel(1))
                .executes(CommandPlane::plane);

        dispatcher.register(commandTile);
    }

    private static int plane(CommandContext<CommandSource> commandSourceCommandContext) {

        GroupObject groupObject = GroupObject.getInstance();
        if(groupObject == null || groupObject.objects == null)
            return 0;

        ClientWorld client = Minecraft.getInstance().world;
        if(client ==null)
            return 0;

        final RayTraceResult rayTraceResult = Minecraft.getInstance().objectMouseOver;
        if(rayTraceResult == null)
            return 0;

       // groupObject.objects.forEach();

        Vector3d hit = rayTraceResult.getHitVec();
        System.out.println("Touched vector:(" + hit.x + ", " + hit.y + ", " + hit.z + ")");
        //client.addParticle(ParticleTypes.BARRIER, (double) hit.x + 0.5D, (double)hit.y + 0.5D, (double)hit.z + 0.5D, 0.0D, 0.0D, 0.0D);

        return 1;
    }

}

