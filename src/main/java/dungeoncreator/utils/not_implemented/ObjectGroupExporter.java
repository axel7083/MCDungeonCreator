package dungeoncreator.utils.not_implemented;

import dungeoncreator.GroupObject;
import dungeoncreator.models.InGameTile;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectGroupExporter {

    private GroupObject groupObject;
    private World world;

    public ObjectGroupExporter(GroupObject groupObject, World world, String directory) {
        this.groupObject = groupObject;
        this.world = world;
    }

    //TODO: code it
    public boolean export() {

        List<String> airBlocks = Arrays.asList("minecraft:air", "minecraft:cave_air");
        List<String> playerHeads = Arrays.asList("minecraft:player_head", "minecraft:player_wall_head");

        //short[] blocks =

        for(InGameTile tile : groupObject.objects) {





        }


        return true;
    }
}
