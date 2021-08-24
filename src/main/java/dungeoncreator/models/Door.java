package dungeoncreator.models;

import dungeoncreator.gui.door.AbstractDoorBlockScreen;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Door {
    public int[] pos;
    public int[] size;
    public ArrayList<String> tiles = new ArrayList<>(); // If size == 1 simple stretch OTHERWISE WILL BE A tile-group OR will be dead-ends if size == 0, tags MUST be enter
    public String tags;
    public float probability = 0f;
    public AbstractDoorBlockScreen.DoorModes mode;

    // Minecraft World real position
    public BlockPos blockPos;

    public Door(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public void set(int[] pos, int[] size, ArrayList<String> tiles, String tags, float probability, AbstractDoorBlockScreen.DoorModes mode) {
        this.pos = pos;
        this.size = size;
        this.tiles = tiles;
        this.tags = tags;
        this.probability = probability;
        this.mode = mode;
    }
}
