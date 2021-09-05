package dungeoncreator.models.dungeons;

import java.util.ArrayList;
import java.util.List;

public class ObjectGroup {
    List<Tile> objects = null;

    public void addObjectTile(Tile tile) {
        if(objects == null)
            objects = new ArrayList<>();

        objects.add(tile);
    }
}
