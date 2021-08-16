package dungeoncreator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dungeoncreator.GroupObject;
import dungeoncreator.managers.TilesManager;
import dungeoncreator.models.TileObject;
import dungeoncreator.utils.TileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;

import java.io.File;
import java.io.IOException;

public class CommandTile {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        //Minecraft.getInstance().gameDir


        LiteralArgumentBuilder<CommandSource> commandTile
                = Commands.literal("tiles")
                .requires((commandSource) -> commandSource.hasPermissionLevel(1))
                .then(Commands.literal("create")
                        .then(Commands.argument("name",StringArgumentType.string())
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .executes(commandContext -> {
                                                    createTile(commandContext);

                                                    //sendMessage(commandContext, "PROVIDER NAME: " + dir);
                                                    return 0;
                                                })))))
                // Deleting a tile
                .then(Commands.literal("delete")
                        .then(Commands.argument("name",StringArgumentType.string())
                                .executes(CommandTile::deleteTile)))
                // Listing all tiles
                .then(Commands.literal("list").executes(CommandTile::listTiles))
                .then(Commands.literal("show").executes(commandSource -> toggleBox(commandSource,true)))
                .then(Commands.literal("hide").executes(commandSource -> toggleBox(commandSource,false)))
                .then(Commands.literal("where").executes(CommandTile::where))
                .then(Commands.literal("export").executes(CommandTile::exportWalkable))
                .then(Commands.literal("walkable").executes(CommandTile::showWalkable)); //TODO: combine show and walkable /show [box|walkable]

        dispatcher.register(commandTile);
    }

    static int exportWalkable(CommandContext<CommandSource> commandContext) {
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        try {
            BlockPos pos = commandContext.getSource().asPlayer().getPosition();
            TileObject t= TileUtils.getTileWithPlayerInside(groupObject.objects, pos.getX(), pos.getY(), pos.getZ());
            if(t != null) {
                TileUtils.exportRegionPlane(t);
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


    static int showWalkable(CommandContext<CommandSource> commandContext) {
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        try {
            BlockPos pos = commandContext.getSource().asPlayer().getPosition();
            TileObject t= TileUtils.getTileWithPlayerInside(groupObject.objects, pos.getX(), pos.getY(), pos.getZ());
            if(t != null) {
                t.displayWalkable = !t.displayWalkable;
                if(!t.heightMapComputed) { //TODO: maybe offer a way to "recompute" it if map changed
                    TileUtils.computeHeightMap(t, commandContext.getSource().getWorld());
                    sendMessage(commandContext,"Computing height map...");
                }
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


    static int where(CommandContext<CommandSource> commandContext) {
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        try {
            BlockPos pos = commandContext.getSource().asPlayer().getPosition();
            TileObject t= TileUtils.getTileWithPlayerInside(groupObject.objects, pos.getX(), pos.getY(), pos.getZ());
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
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        TilesManager.getInstance(saveDir).toggleTilesBox(visible);
        return 1;
    }

    static int deleteTile(CommandContext<CommandSource> commandContext) {
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        sendMessage(commandContext,groupObject.deleteTile(StringArgumentType.getString(commandContext,"name")));
        return 1;
    }

    static File getSaveDirectory() {
        if(Minecraft.getInstance().isIntegratedServerRunning()) {
            return Minecraft.getInstance().getIntegratedServer().getWorldIconFile().getParentFile();
        }
        return null;
    }

    static int listTiles(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return 0;
        }
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        Entity entity = commandContext.getSource().getEntity();
        if(entity != null)
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(groupObject.listAllTiles(), ChatType.CHAT, entity.getUniqueID());
        return 1;
    }

    static void createTile(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
        BlockPos from = BlockPosArgument.getLoadedBlockPos(commandContext, "from");
        BlockPos to = BlockPosArgument.getLoadedBlockPos(commandContext, "to");
        String name = StringArgumentType.getString(commandContext,"name");

        File saveDir = getSaveDirectory();
        if(saveDir == null) {
            sendMessage(commandContext,"[Error] saveDir NULL");
            return;
        }
        sendMessage(commandContext,"saveDir: " + saveDir.getAbsolutePath());

        GroupObject groupObject = GroupObject.getInstance(saveDir);
        try {
            groupObject.addTile(new TileObject(name, new int[] {from.getX(),from.getY(),from.getZ()},new int[] {to.getX(),to.getY(),to.getZ()}));
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

