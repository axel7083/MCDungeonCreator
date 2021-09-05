package dungeoncreator.models;

import dungeoncreator.gui.door.AbstractDoorBlockScreen;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class InGameDoor {
    public int[] pos = new int[] {0,0,0};
    public int[] size = new int[] {0,0,0};
    public ArrayList<String> tiles = new ArrayList<>(); // If size == 1 simple stretch OTHERWISE WILL BE A tile-group OR will be dead-ends if size == 0, tags MUST be enter
    public String tags = "";
    public String name = "";
    public float probability = 0f;
    public AbstractDoorBlockScreen.DoorModes doorModes;
    public AbstractDoorBlockScreen.TagsModes tagsModes;

    // Minecraft World real position
    public BlockPos blockPos;

    public InGameDoor(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void set(int[] pos, int[] size, ArrayList<String> tiles, String tags, float probability, AbstractDoorBlockScreen.DoorModes mode, AbstractDoorBlockScreen.TagsModes tagsModes) {
        this.pos = pos;
        this.size = size;
        this.tiles = tiles;
        this.tags = tags;
        this.probability = probability;
        this.doorModes = mode;
        this.tagsModes = tagsModes;
    }
}
