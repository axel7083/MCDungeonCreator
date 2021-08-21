package dungeoncreator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import dungeoncreator.utils.TileUtils;
import dungeoncreator.utils.not_implemented.ObjectGroupExporter;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;

import java.io.File;
import java.io.IOException;

public class CommandTile {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandTile =
                Commands.literal("tiles")
                .requires((commandSource) -> commandSource.hasPermissionLevel(1))
                .then(Commands.literal("create")
                        .then(Commands.argument("name",StringArgumentType.string())
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .executes(commandContext -> {
                                                    createTile(commandContext);
                                                    return 0;
                                                })))))
                // Deleting a tile
                .then(Commands.literal("delete")
                        .then(Commands.argument("name",StringArgumentType.string())
                                .executes(CommandTile::deleteTile)))
                // Listing all tiles
                .then(Commands.literal("list").executes(CommandTile::listTiles))
                // Show walkable areas or tiles boxes
                .then(Commands.literal("boxes")
                        .then(Commands.literal("show").executes(commandSource -> toggleBox(commandSource,true)))
                        .then(Commands.literal("hide").executes(commandSource -> toggleBox(commandSource,false))))
                .then(Commands.literal("where").executes(CommandTile::where))
                .then(Commands.literal("force-save").executes(CommandTile::forceSave))
                .then(Commands.literal("export").executes(CommandTile::export)
                        .then(Commands.literal("set-default-path")
                            .then(Commands.argument("path",StringArgumentType.string())
                                    .executes(CommandTile::setExportPath)))
                        .then(Commands.literal("reset-default-path")
                                .executes(CommandTile::resetExportPath))
                        .then(Commands.literal("show-export-path")
                                .executes(CommandTile::showExportPath)))
                .then(Commands.literal("walkable")
                        .then(Commands.literal("show").executes(commandSource -> toggleWalkable(commandSource,true)))
                        .then(Commands.literal("hide").executes(commandSource -> toggleWalkable(commandSource,false)))
                );

        dispatcher.register(commandTile);
    }

    static int showExportPath(CommandContext<CommandSource> commandContext) {

        if(Cache.getInstance().worldData.exportPath == null) {
            sendMessage(commandContext,
                    "For this world, we use the default export path which is: " + Cache.getInstance().worldData.saveDir.getAbsolutePath() + "\\" + Cache.getInstance().worldData.fileName);
        }
        else
        {
            sendMessage(commandContext,"The current export-path is " + Cache.getInstance().worldData.exportPath);
        }

        return 1;
    }

    static int resetExportPath(CommandContext<CommandSource> commandContext) {
        try {
            Cache.getInstance().worldData.exportPath = null;
            Cache.getInstance().worldData.save();
            sendMessage(commandContext,"Saved.");
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(commandContext,"Error while defining the path");
        }
        return 1;
    }

    static int setExportPath(CommandContext<CommandSource> commandContext) {
        String path = StringArgumentType.getString(commandContext,"path");
        try {
            File file = new File(path);

            if (!file.isDirectory()) {
                Cache.getInstance().worldData.exportPath = path;
                Cache.getInstance().worldData.save();
                sendMessage(commandContext,"Saved.");
            }
            else
            {
                sendMessage(commandContext,"The path selected is a directory, you need to choose the filename.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(commandContext,"Error while defining the path");
        }
        return 1;
    }

    static int recomputeHeightMap(CommandContext<CommandSource> commandContext) {
        // Fetching the cahce
        Cache cache = Cache.getInstance();

        // Get the name argument
        String name = StringArgumentType.getString(commandContext,"name");

        if(name == null) {
            sendMessage(commandContext,"Computing heightmap of all tiles.");
            for(InGameTile t : cache.worldData.objects) {
                TileUtils.computeHeightMap(t, commandContext.getSource().getWorld());
            }
            sendMessage(commandContext,"Done.");
        }
        else
        {
            InGameTile t = cache.worldData.getTileByName(name);
            if(t != null)
                TileUtils.computeHeightMap(t, commandContext.getSource().getWorld());
            else
                sendMessage(commandContext,"Tile with name " + name + " not found.");
        }
        return 1;
    }

    static int forceSave(CommandContext<CommandSource> commandContext) {
        try {
            Cache.getInstance().worldData.save();
            sendMessage(commandContext,"Saved.");
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(commandContext,"Error while saving");
        }
        return 1;
    }

    static int toggleWalkable(CommandContext<CommandSource> commandContext, boolean visible) {
        Cache cache = Cache.getInstance();

        try {
            BlockPos pos = commandContext.getSource().asPlayer().getPosition();
            InGameTile t = TileUtils.getTileWithPlayerInside(cache.worldData.objects, pos.getX(), pos.getY(), pos.getZ());
            if(t != null) {
                t.displayWalkable = visible;
                sendMessage(commandContext,"Computing height map...");
                TileUtils.computeHeightMap(t, commandContext.getSource().getWorld());
                sendMessage(commandContext,"Done.");
            }
            else
            {
                sendMessage(commandContext,"[Error] You are not in a defined tile");
            }

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            sendMessage(commandContext,"[Error] during search");
        }

        return 1;
    }

    static int export(CommandContext<CommandSource> commandContext) {
        sendMessage(commandContext, "Any objectgroup.json file inside the world save folder will be deleted.");
        System.out.println("EXPORTING");
        Cache cache = Cache.getInstance();
        ObjectGroupExporter o = new ObjectGroupExporter(cache.worldData, commandContext.getSource().getWorld(), cache.worldPath, str -> sendMessage(commandContext, str));
        o.export();

        return 1;
    }

    static int where(CommandContext<CommandSource> commandContext) {
        System.out.println("WHERE");
        Cache cache = Cache.getInstance();
        System.out.println("Cache.getInstance() => "  + cache.worldData.objects.size());
        try {
            BlockPos pos = commandContext.getSource().asPlayer().getPosition();
            InGameTile t= TileUtils.getTileWithPlayerInside(cache.worldData.objects, pos.getX(), pos.getY(), pos.getZ());
            if(t == null) {
                sendMessage(commandContext,"You are not currently inside a defined tile.");
            }
            else
            {
                sendMessage(commandContext,"You are currently in tile: " + t.id);
            }

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            sendMessage(commandContext,"[Error] during search");
        }

        return 1;
    }

    static int toggleBox(CommandContext<CommandSource> commandContext, boolean visible) {
        Cache.getInstance().worldData.objects.forEach(tileObject -> tileObject.visible = visible);
        return 1;
    }

    static int deleteTile(CommandContext<CommandSource> commandContext) {
        sendMessage(commandContext,Cache.getInstance().worldData.deleteTile(StringArgumentType.getString(commandContext,"name")));
        return 1;
    }

    static int listTiles(CommandContext<CommandSource> commandContext) {
        Entity entity = commandContext.getSource().getEntity();
        if(entity != null)
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(Cache.getInstance().worldData.listAllTiles(), ChatType.CHAT, entity.getUniqueID());
        return 1;
    }

    static void createTile(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        BlockPos from = BlockPosArgument.getLoadedBlockPos(commandContext, "from");
        BlockPos to = BlockPosArgument.getLoadedBlockPos(commandContext, "to");
        String name = StringArgumentType.getString(commandContext,"name");

        try {
            Cache.getInstance().worldData.addTile(new InGameTile(name, new int[] {from.getX(),from.getY(),from.getZ()},new int[] {to.getX(),to.getY(),to.getZ()}));
            sendMessage(commandContext, "Saved.");
        } catch (IOException e) {
            sendMessage(commandContext, "[Error] " + e.getMessage());
            e.printStackTrace();
        }
    }

    static int sendMessage(CommandContext<CommandSource> commandContext,IFormattableTextComponent finalText) {
        Entity entity = commandContext.getSource().getEntity();
        if (entity != null) {
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.CHAT, entity.getUniqueID());
            //func_232641_a_ is sendMessage()
        } else {
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.SYSTEM, Util.DUMMY_UUID);
        }
        return 1;
    }

    static int sendMessage(CommandContext<CommandSource> commandContext, String message) {
        TranslationTextComponent finalText = new TranslationTextComponent("chat.type.announcement",
                commandContext.getSource().getDisplayName(), new StringTextComponent(message));
        return sendMessage(commandContext,finalText );
    }
}

