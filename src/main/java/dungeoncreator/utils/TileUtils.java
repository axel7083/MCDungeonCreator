package dungeoncreator.utils;

import dungeoncreator.models.TileObject;

import java.util.ArrayList;

public class TileUtils {

    public static boolean checkOverLapping(ArrayList<TileObject> tiles, TileObject tile) {
        for(TileObject t : tiles) {
            if(t.isOverlapping(tile))
                return true;
        }
        return false;
    }
}
