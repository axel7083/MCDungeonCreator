package dungeoncreator.models;

public class TileObject {
    public transient boolean visible = false;
    public String id;
    public int[] pos;
    public int[] pos2;

    public TileObject(String id, int[] pos, int[] pos2) {
        this.id = id;
        this.pos = pos;
        this.pos2 = pos2;
    }

    public boolean isOverlapping(TileObject o2) {
        return pos2[0] > o2.pos[0] && pos[0] < o2.pos2[0] &&
                pos2[1]  > o2.pos[1] && pos[1] < o2.pos2[1] &&
                pos2[2]  > o2.pos[2] && pos[2] < o2.pos2[2];
    }
}
