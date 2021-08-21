package dungeoncreator.exporter;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;
import dungeoncreator.WorldData;
import dungeoncreator.models.InGameTile;
import dungeoncreator.models.not_implemented.Door;
import dungeoncreator.models.not_implemented.ObjectGroup;
import dungeoncreator.models.ObjectTile;
import dungeoncreator.models.not_implemented.Region;
import dungeoncreator.utils.TileUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.StructureBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ObjectGroupExporter {

    private WorldData worldData;
    private World world;
    private JsonObject blockMap;
    private String saveDir;
    private Callback callback;

    public ObjectGroupExporter(WorldData worldData, World world, @Nonnull String saveDir, @Nullable Callback callback) {
        this.worldData = worldData;
        this.world = world;
        this.blockMap = getBlockMap();
        this.saveDir = saveDir;
        this.callback = callback;
    }

    private int[] findMatchingBlock(String blockName, Map<String, String> properties) {
        JsonElement e = blockMap.get(blockName);
        if(e == null) {
            return null;
        }

        JsonArray a = e.getAsJsonArray();

        if(a.get(0).isJsonPrimitive()) {
            return new int[]{a.get(0).getAsInt(), a.get(1).getAsInt()};
        }
        else
        {
            for(int i = 0 ; i < a.size(); i++) {
                // Fetch every specific version of the block (depending on properties)
                JsonObject blockVersion = a.get(i).getAsJsonObject();

                // Fetch the properties of the block
                JsonObject props = blockVersion.get("props").getAsJsonObject();

                boolean valid = true;
                // iterating every set of entry in the HashMap.
                for (Map.Entry<String, String> set : properties.entrySet()) {
                    JsonElement js = props.get(set.getKey());
                    if (js != null)
                        valid &= set.getValue().equals(js.getAsString());
                }

                if(valid) {
                    JsonArray d = blockVersion.get("dungeons").getAsJsonArray();
                    //System.out.println(d);

                    // TODO: fix bug => When DungeonBlocks HAS 3 block properties EXAMPLE:minecraft:jungle_leaves

                    int size = d.size();
                    if(size == 1) {
                        return new int[]{d.get(0).getAsInt(), 0};
                    }
                    else if(size >= 2) {
                        return new int[]{d.get(0).getAsInt(), d.get(1).getAsInt()};
                    }
                    else
                    {
                        System.out.println("Cannot find dungeons id and block data: " + blockName);
                        return new int[]{0, 0};
                    }
                }
            }
        }

        return null;
    }

    public boolean export() {

        try {

            ObjectGroup objectGroup = new ObjectGroup();
            for(InGameTile tile : worldData.objects) {
                ArrayList<Region> regions = new ArrayList<>();
                ArrayList<Door> doors = new ArrayList<>();

                int block_count =tile.sizeX*tile.sizeY*tile.sizeZ;
                byte[] blocks = new byte[(int) Math.ceil(block_count*2 + (float) block_count/2)];
                Arrays.fill(blocks, (byte) 0);

                for(int x = 0 ; x < tile.sizeX; x++) {
                    for(int y = 0; y < tile.sizeY; y++) {
                        for(int z = 0 ; z < tile.sizeZ; z++) {

                            int realX = x+Math.min(tile.pos[0],tile.pos2[0]);
                            int realY = y+Math.min(tile.pos[1],tile.pos2[1]);
                            int realZ = z+Math.min(tile.pos[2],tile.pos2[2]);

                            BlockState block = world.getBlockState(
                                    new BlockPos(realX,realY,realZ));
                            String blockName = Objects.requireNonNull(block.getBlock().getRegistryName()).toString();
                            Map<String, String> properties = new HashMap<>();
                            block.getValues().forEach((p, v) -> {
                                properties.put(p.getName(), v.toString());
                            });

                            switch (blockName) {
                                case "minecraft:air":
                                case "minecraft:cave_air":
                                    continue;
                                case "minecraft:player_head":
                                case "minecraft:player_wall_head":
                                    System.out.println("Define player spawn position");
                                    regions.add(new Region("playerstart", "playerstart", "trigger", new int[]{x, y, z}, new int[]{1,1,1}));
                                    break;
                                case "minecraft:structure_block":

                                    TileEntity tileentity = world.getTileEntity(new BlockPos(realX, realY, realZ));
                                    // We fetch the tileEntity linked to the structureBlock at the position we are currently exploring
                                    if (tileentity instanceof StructureBlockTileEntity) {
                                        BlockPos boxSize = ((StructureBlockTileEntity) tileentity).getStructureSize();
                                        String name = ((StructureBlockTileEntity) tileentity).getName();
                                        String metadata = ((StructureBlockTileEntity) tileentity).getMetadata();
                                        String tag = null;
                                        String type = null;

                                        int[] pos = new int[]{x, y, z};
                                        int[] size = new int[]{boxSize.getX(), boxSize.getY(), boxSize.getZ()};

                                        // We create a door if it is
                                        if(name.startsWith("door:")) {
                                            doors.add(new Door(pos, size));
                                        }

                                        // If it is a region, we define it properly
                                        if(name.startsWith("region:")) {
                                            if(name.length() > 7)
                                                name = name.substring(7);

                                            JsonObject json_metadata = null;
                                            if(metadata.length() > 2) {
                                                try {
                                                    json_metadata = new Gson().fromJson(metadata, JsonObject.class);
                                                }
                                                catch (JsonSyntaxException e) {
                                                    if(callback != null) {
                                                        callback.LogEvent("[ERROR] JsonSyntaxException in StructureBlock at (" + realX + "," + realY + "," + realZ + ").");
                                                    }
                                                }
                                            }

                                            if(json_metadata != null) {
                                                if(json_metadata.has("tags"))
                                                    tag = json_metadata.get("tags").getAsString();
                                                if(json_metadata.has("type"))
                                                    type = json_metadata.get("type").getAsString();
                                            }

                                            regions.add(new Region(name, tag, type, pos, size));
                                        }
                                    }


                                case "minecraft:barrier":
                                    // TODO:
                                    break;
                                default:
                                    // Find matching blocks
                                    int[] dungeonBlock = findMatchingBlock(blockName, properties);

                                    if(dungeonBlock != null) {
                                        //System.out.println("We got [" + dungeonBlock[0] + "," + dungeonBlock[1] + "]");

                                        int index = ((y * tile.sizeZ + z) * tile.sizeX + x);
                                        blocks[2*index] = (byte) (dungeonBlock[0] >> 8);
                                        blocks[2*index+1] = (byte) (dungeonBlock[0] & 0xff);

                                        int dataIndex = (int) Math.floor(block_count*2 + (float) index/2) ;
                                        //Odd number (impaire)
                                        if((index & 1) == 1) {
                                            blocks[dataIndex] = (byte) (blocks[dataIndex] | dungeonBlock[1] & 0xf);
                                        }
                                        else //Even number (pair)
                                        {
                                            blocks[dataIndex] = (byte) (dungeonBlock[1] << 4 | blocks[dataIndex]);
                                        }
                                    }
                                    else {
                                        if(callback != null) {
                                            callback.LogEvent("WARNING: " + blockName + " in position (" + realX + "," + realY + "," + realZ + ") has no corresponding block.");
                                        }
                                    }
                                    break;

                            }

                        }
                    }
                }

                String block = new String(Base64.getEncoder().encode(TileUtils.compress(blocks)));
               // System.out.println("OUTPUT: " + block);

                objectGroup.addObjectTile(new ObjectTile(tile.id,
                        new int[]{tile.sizeX, tile.sizeY, tile.sizeZ},
                        new int[]{Math.min(tile.pos[0],tile.pos2[0]), Math.min(tile.pos[1],tile.pos2[1]), Math.min(tile.pos[2],tile.pos2[2])},
                        block,
                        TileUtils.exportRegionPlane(tile),
                        TileUtils.exportHeightPlane(tile),
                        null,
                        doors,
                        regions));
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(objectGroup);

            BufferedWriter writer = new BufferedWriter( new FileWriter((worldData.exportPath==null)?(saveDir + "\\" + "objectgroup.json"):worldData.exportPath));
            writer.write(json);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Export finish");
        if(callback != null) {
            callback.LogEvent("Export Done.");
        }
        return true;
    }

    public interface Callback {
        void LogEvent(String content);
    }

    // TODO: move in an Utils file
    private static JsonObject getBlockMap() {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ObjectGroupExporter.class.getClassLoader()
                .getResourceAsStream("assets/dungeoncreator/blockmap.json")), StandardCharsets.UTF_8));
        return gson.fromJson(reader, JsonObject.class);
    }
}
