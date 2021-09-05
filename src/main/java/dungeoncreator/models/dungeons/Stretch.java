package dungeoncreator.models.dungeons;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Stretch {
    ArrayList<String> tiles;

    @SerializedName(value = "prop-groups")
    ArrayList<String> prop_groups;

    @SerializedName(value = "dead-ends")
    ArrayList<String> dead_ends;

    Mobs mobs;

    public static class Mobs {
        public ArrayList<String> only;
    }
}
