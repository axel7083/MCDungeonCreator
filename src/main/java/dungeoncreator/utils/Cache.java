package dungeoncreator.utils;

import dungeoncreator.WorldData;
import dungeoncreator.models.InGameDoor;
import net.minecraft.world.IWorld;

import java.util.ArrayList;

public class Cache {

    public IWorld currentWorld = null;
    public WorldData worldData = null;
    public String worldPath = null;

    public ArrayList<InGameDoor> deleted = new ArrayList<>();

    private static Cache cache = null;


    private Cache() {

    }

    public void destroy() {
        currentWorld = null;
        worldData = null;
    }

    public static Cache getInstance() {
        if(cache == null)
            cache = new Cache();
        return cache;
    }
}
