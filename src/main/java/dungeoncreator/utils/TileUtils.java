package dungeoncreator.utils;

import dungeoncreator.WorldData;
import dungeoncreator.models.InGameTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class TileUtils {

    public static boolean checkOverLapping(ArrayList<InGameTile> tiles, InGameTile tile) {
        for(InGameTile t : tiles) {
            if(t.isOverlapping(tile))
                return true;
        }
        return false;
    }

    public static InGameTile getTileWithPlayerInside(ArrayList<InGameTile> tiles, int playerX, int playerY, int playerZ) {
        for(InGameTile t : tiles) {
            if(t.sizeX == 0 || t.sizeY == 0 || t.sizeZ == 0)
                t.computeSizes();

            if(t.minX < playerX && t.sizeX + t.minX > playerX
            && t.minY < playerY && t.sizeY + t.minY > playerY
            && t.minZ < playerZ && t.sizeZ + t.minZ > playerZ)
                return t;
        }
        return null;
    }

    public static NativeImage computeHeightMap(InGameTile t, World world) {

        if(t.sizeX == 0 || t.sizeY == 0 || t.sizeZ == 0)
            t.computeSizes();

        int textureSize = 32;

        float ratio = Math.max(t.sizeX, t.sizeZ);
        ratio = textureSize/ratio;
        System.out.println("Ratio " + ratio);

        NativeImage textureArray = new NativeImage(textureSize, textureSize, true);

        if(world == null)
            return textureArray;

        for(int x = 0 ; x < t.sizeX; x++) {
            for(int z = 0; z < t.sizeZ; z++) {

                short y = 256;
                BlockState s;

                do {
                    y--;
                    s = world.getBlockState(new BlockPos(x+t.minX, y, z+t.minZ));
                } while((!s.isSolid() && !s.getMaterial().isLiquid()) && y != 0);


                int colorRGC = s.getMaterial().getColor().colorValue;
                int colorRGBA = NativeImage.getCombined(255,
                        colorRGC & 255,
                        (colorRGC >> 8) & 255,
                        (colorRGC >> 16) & 255);

                textureArray.setPixelRGBA((int)(x*ratio), (int)(z*ratio),  colorRGBA);

                t.heightPlane[x][z] = y;
            }
        }

        return textureArray;
    }

    public static String setBlockWalkable(PlayerEntity playerIn, byte value, int range) {
        WorldData worldData = WorldData.getInstance();
        if(worldData != null) {
            // Fetching the player position
            BlockPos pos = playerIn.getPosition();

            // Getting the tile where the player is in
            InGameTile tile = TileUtils.getTileWithPlayerInside(worldData.objects,pos.getX(),pos.getY(),pos.getZ());

            if(tile == null)  {
                return "You are not currently in a tile.";
            }
            else
            {
                // Getting what the player is focusing in
                final RayTraceResult rayTraceResult = playerIn.pick(50.0D, 0.0F, false);
                if(rayTraceResult.getType() != RayTraceResult.Type.BLOCK) {
                    return "Touching nothing";
                }
                else
                {
                    Vector3d hit = rayTraceResult.getHitVec();
                    if(tile.minX<hit.x && tile.minX+tile.sizeX-1 > hit.x
                            && tile.minZ<hit.z && tile.minZ+tile.sizeZ-1 > hit.z) {


                        for(int i = 0 ; i < range ; i++) {
                            for(int j = 0 ; j < range ; j++) {
                                int x = (int) (hit.x-tile.minX) + i - range/2;
                                int z = (int) (hit.z-tile.minZ) + j - range/2;

                                if(x < tile.sizeX && z < tile.sizeZ  && x >=0 && z >=0)
                                    tile.regionPlane[x][z] = value;
                            }
                        }

                    }
                    else {
                        return "Target is outside the tile";
                    }
                }
            }
        }
        else
            return "Please load the GroupObject first /tiles load";


        return null;
    }

    public static String exportRegionPlane(InGameTile tile) {

        byte[][] plane = tile.regionPlane;
        byte[] simpleArray = new byte[tile.sizeX*tile.sizeZ];

        for(int x = 0 ; x < tile.sizeX; x++) {
            for(int z = 0; z < tile.sizeZ ; z++) {
                simpleArray[x+z*tile.sizeX] = plane[x][z];
            }
        }

        try {
            return new String(Base64.getEncoder().encode(compress(simpleArray)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //heightPlane
    public static String exportHeightPlane(InGameTile tile) {

        byte[] simpleArray = new byte[tile.sizeX*tile.sizeZ];

        for(int x = 0 ; x < tile.sizeX; x++) {
            for(int z = 0; z < tile.sizeZ ; z++) {
                simpleArray[x+z*tile.sizeX] = (byte) (tile.heightPlane[x][z] & 0xff);
            }
        }

        try {
            return new String(Base64.getEncoder().encode(compress(simpleArray)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void importRegionPlane(InGameTile tile) throws IOException, DataFormatException {
        if(tile.encodedRegionPlane == null) {
            return;
        }

        byte[][] plane = new byte[tile.sizeX][tile.sizeZ];
        byte[] simpleArray = decompress(Base64.getDecoder().decode(tile.encodedRegionPlane));

        for(int x = 0 ; x < tile.sizeX; x++) {
            for(int z = 0; z < tile.sizeZ ; z++) {
                plane[x][z] = simpleArray[x+z*tile.sizeX];
            }
        }
        tile.regionPlane = plane;
    }

    // Source: https://dzone.com/articles/how-compress-and-uncompress
    public static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();

        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }



}
