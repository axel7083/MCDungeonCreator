package dungeoncreator.utils;

import dungeoncreator.WorldData;
import net.minecraft.world.IWorld;

public class Cache {

    public IWorld currentWorld = null;
    public WorldData worldData = null;
    public String worldPath = null;

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
