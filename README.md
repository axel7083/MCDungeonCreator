![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_show.png)

This mod need a lot of re-work, most of the features are just prototype to prove it is possible to have them.

# MCDungeonCreator
 A Minecraft Java Mod to create objectgroup.json from command line. To use the mod, install forge 1.16.5 from [here](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.16.5.html) and download the `dungeoncreator-1.16.5.jar` file of the mod in the [release section](https://github.com/DungeonsModding/MCDungeonCreator/releases/latest) in the Assets part.
 
# Objectgroup.json file generated

The objectgroup.json does not contain the block information, only the position of the tiles, you need to use [Dungeons-Mod-Kit](https://github.com/Dokucraft/Dungeons-Mod-Kit) and the [JavaWorldToObjectGroup.exe](https://github.com/Dokucraft/Dungeons-Mod-Kit/tree/master/Tools) tool to convert to an objectgroup.json containing the block informations.

An objectgroup.json generate from this mod will look like this:
 ```JS
{
  "objects": [
    {
      "id": "name",
      "pos": [
        X1,
        Y1,
        Z1
      ],
      "pos2": [
        X2,
        Y2,
        Z2
      ],
      "region-plane" = "...",
    }, ...
  ]
}
```
 
# Region-Plane (For proper minimap)
![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/walkable_area.png)

You can use the command `/tiles walkable show` to see the walkable areas in the tiles you are currently in (See example [here](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/walkable_area.png) ).
Two items have been added to the Redstone inventory section in creative, they allow you to paint the walkable areas (when visible) in Green or Red.

In the save directory of the world you are currently in, in the objectgroup.json each objects will have (if used) a new property "region-plane" (You can do `/tiles force-save` to force the mod to generate the file) you need to replace in the final objectgroup.json generate by the [JavaWorldToObjectGroup.exe](https://github.com/Dokucraft/Dungeons-Mod-Kit/tree/master/Tools) tool the content of the region-plane property by the one from the Java World Directory.

## TODO:
The goal would be to avoid using [JavaWorldToObjectGroup.exe](https://github.com/Dokucraft/Dungeons-Mod-Kit/tree/master/Tools) and do all the conversion in the Java Mod. It is possible, but a bit long to port all the Python code. Any help appreciated.
 
# Commands

| Command                                 	| Description                                                                                     	|
|-----------------------------------------	|-------------------------------------------------------------------------------------------------	|
| `tiles create [name] X1 Y1 Z1 X2 Y2 Z2` 	| Create a tile in the objectgroup.json file in the root folder of the save you are currently in. ![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_create.png) 	|
| `tiles delete [name]`                   	| Delete a tile from the objectgroup.json file.                                                    	|
| `tiles boxes [show|hide]`               	| Show or hide boxes around the tiles                                                                     	|
| `tiles walkable [show|hide]`               	| Show or hide the walkable areas on the tiles                                                   	|
| `tiles list`                            	| List all tiles in the objectgroup.json file ![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_list.png)                                                    	|
| `tiles force-save`               	| Generate directly the objectgroup.json in the world save directory                                                                     	|
