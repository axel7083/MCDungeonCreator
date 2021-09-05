package dungeoncreator.models.dungeons;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Level {
    String id;

    @SerializedName(value = "require-matching-doors")
    boolean require_matching_doors;

    @SerializedName(value = "resource-packs")
    ArrayList<String> resource_packs;

    @SerializedName(value = "visual-theme")
    String visual_theme;

    @SerializedName(value = "object-groups")
    ArrayList<String> object_groups;

    @SerializedName(value = "ambience-level-id")
    String ambience_level_id;
}
