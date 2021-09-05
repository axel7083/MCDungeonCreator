package dungeoncreator.models.dungeons;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tile {

    String id;
    int[] size;
    int[] pos;
    String blocks;
    @SerializedName(value = "region-plane")
    String regionPlane;
    @SerializedName(value = "height-plane")
    String heightPlane;
    @SerializedName(value = "region-y-plane")
    String regionYPlane;
    List<Door> doors;
    List<Region> regions;

    public Tile(String id, int[] size, int[] pos, String blocks, String regionPlane, String heightPlane, String regionYPlane, List<Door> doors, List<Region> regions) {
        this.id = id;
        this.size = size;
        this.pos = pos;
        this.blocks = blocks;
        this.regionPlane = regionPlane;
        this.heightPlane = heightPlane;
        this.regionYPlane = (regionYPlane==null)?heightPlane:regionYPlane; // Usually just a copy of heightPlane
        this.doors = doors;
        this.regions = regions;
    }
}
