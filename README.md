![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_show.png)


# MCDungeonCreator
 A Minecraft Java Mod to create objectgroup.json from command line.
 
# Objectgroup.json file generated

The objectgroup.json does not contain the block information, only the position of the tiles, you need to use (Dungeons-Mod-Kit)[https://github.com/Dokucraft/Dungeons-Mod-Kit] and the (JavaWorldToObjectGroup.exe)[https://github.com/Dokucraft/Dungeons-Mod-Kit/tree/master/Tools] tool to convert to an objectgroup.json containing the block informations.

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
      ]
    }, ...
  ]
}
```
 
# Commands

| Command                                 	| Description                                                                                     	|
|-----------------------------------------	|-------------------------------------------------------------------------------------------------	|
| `tiles create [name] X1 Y1 Z1 X2 Y2 Z2` 	| Create a tile in the objectgroup.json file in the root folder of the save you are currently in. ![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_create.png) 	|
| `tiles delete [name]`                   	| Delete a tile from the objectgroup.json file.                                                    	|
| `tiles show`                            	| Show a box around the tiles                                                                     	|
| `tiles hide`                            	| Hide the box around the tiles                                                                   	|
| `tiles list`                            	| List all tiles in the objectgroup.json file ![alt text](https://github.com/DungeonsModding/MCDungeonCreator/blob/main/screenshots/tiles_list.png)                                                    	|
