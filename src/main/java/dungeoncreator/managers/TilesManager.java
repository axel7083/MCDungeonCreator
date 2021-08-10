package dungeoncreator.managers;

import dungeoncreator.GroupObject;

import java.io.File;

public class TilesManager {
    transient private static File saveDir = null;

    private static TilesManager tilesManager = null;
    private TilesManager() {}

    public void toggleTilesBox(boolean value) {
        GroupObject groupObject = GroupObject.getInstance(saveDir);
        if(groupObject.objects != null)
            groupObject.objects.forEach(tileObject -> tileObject.visible = value);
    }

    /**
     * Return the singleton TilesManager instance for the world
     */
    public static TilesManager getInstance(File _saveDir) {
        if(tilesManager == null || !saveDir.equals(_saveDir)) {
            saveDir = _saveDir;
            tilesManager = new TilesManager();
        }
        return tilesManager;
    }
}
