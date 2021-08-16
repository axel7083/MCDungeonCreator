package dungeoncreator.models;

public class TileObject {
    public transient boolean visible = false;
    public transient boolean displayWalkable = false;
    public String id;
    public int[] pos;
    public int[] pos2;
    public int sizeX;
    public int sizeY;
    public int sizeZ;

    public int minX;
    public int minY;
    public int minZ;

    public byte[][] regionPlane;
    public transient short[][] heightPlane;
    public transient boolean heightMapComputed = false;

    public TileObject(String id, int[] pos, int[] pos2) {
        this.id = id;
        this.pos = pos;
        this.pos2 = pos2;
        generatePlane();
        computeSizes();
    }

    public void computeSizes() {
        sizeX = Math.abs(pos[0] - pos2[0])+1;
        sizeY = Math.abs(pos[1] - pos2[1])+1;
        sizeZ = Math.abs(pos[2] - pos2[2])+1;

        minX = Math.min(pos[0],pos2[0]);
        minY = Math.min(pos[1],pos2[1]);
        minZ = Math.min(pos[2],pos2[2]);
    }

    public void generatePlane() {
        // The region plane is
        regionPlane = new byte[sizeX][sizeZ];
        heightPlane = new short[sizeX][sizeZ];
        for(int x = 0; x < sizeX; x ++)
            for(int z = 0; z < sizeZ ; z++) {
                regionPlane[x][z] = 1;
                heightPlane[x][z] = 255;
            }
    }

    public boolean isOverlapping(TileObject o2) {
        return pos2[0] > o2.pos[0] && pos[0] < o2.pos2[0] &&
                pos2[1]  > o2.pos[1] && pos[1] < o2.pos2[1] &&
                pos2[2]  > o2.pos[2] && pos[2] < o2.pos2[2];
    }
}
