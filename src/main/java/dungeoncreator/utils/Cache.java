package dungeoncreator.utils;

import dungeoncreator.GroupObject;
import dungeoncreator.models.not_implemented.ObjectGroup;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Cache {

    public IWorld currentWorld = null;
    public GroupObject groupObject = null;

    private static Cache cache = null;

    private Cache() {

    }

    public void destroy() {
        currentWorld = null;
        groupObject = null;
    }

    public static Cache getInstance() {
        if(cache == null)
            cache = new Cache();
        return cache;
    }
}
