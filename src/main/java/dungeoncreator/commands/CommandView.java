package dungeoncreator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dungeoncreator.GroupObject;
import dungeoncreator.managers.TilesManager;
import dungeoncreator.models.TileObject;
import dungeoncreator.utils.OrthoViewHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.File;
import java.io.IOException;

public class CommandView {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandTile
                = Commands.literal("simulate")
                .requires((commandSource) -> commandSource.hasPermissionLevel(1))
                .executes(CommandView::simulate);

        dispatcher.register(commandTile);
    }

    private static int simulate(CommandContext<CommandSource> commandSourceCommandContext) {
        OrthoViewHandler ovh = OrthoViewHandler.getInstance();
        ovh.toggle();
        return 1;
    }

}

