package dungeoncreator.models.dungeons;

public class Region {
    String name;
    String tags;
    String type;
    int[] pos;
    int[] size;

    public Region(String name, String tags, String type, int[] pos, int[] size) {
        this.name = name;
        this.tags = tags;
        this.type = type;
        this.pos = pos;
        this.size = size;
    }
}
