package dungeoncreator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dungeoncreator.utils.OrthoViewHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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

