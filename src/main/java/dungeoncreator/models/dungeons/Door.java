package dungeoncreator.models.dungeons;

import dungeoncreator.gui.door.AbstractDoorBlockScreen;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Door {
    public int[] pos;
    public int[] size;
    public String tags;
    public String name;

    public Door(int[] pos, int[] size, String tags, String name) {
        this.pos = pos;
        this.size = size;
        this.tags = tags;
        this.name = name;
    }
}
