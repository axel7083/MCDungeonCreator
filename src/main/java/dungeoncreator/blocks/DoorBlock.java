package dungeoncreator.blocks;

import dungeoncreator.gui.door.DoorBlockScreen;
import dungeoncreator.models.InGameDoor;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import dungeoncreator.utils.TileUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class DoorBlock extends Block {

    public DoorBlock() {
        super(Block.Properties.create(Material.ROCK));
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        System.out.println("onBlockActivated");

        for(InGameTile t : Cache.getInstance().worldData.objects)
            t.computeMinimap(worldIn);

        Minecraft.getInstance().displayGuiScreen(new DoorBlockScreen(state, pos, player));

        return ActionResultType.SUCCESS;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        System.out.println("Block harvested");
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!worldIn.isRemote) {
            if (placer != null) {
                Cache cache = Cache.getInstance();

                InGameTile tile = TileUtils.getTileWithByPosition(cache.worldData.objects, pos.getX(), pos.getY(), pos.getZ());
                if(tile == null) {
                    worldIn.destroyBlock(pos, false);
                    placer.sendMessage(new StringTextComponent("The door block need to be placed inside a tile."), placer.getUniqueID());
                }
                else
                {
                    if(tile.inGameDoors == null)
                        tile.inGameDoors = new ArrayList<>();
                    tile.inGameDoors.add(new InGameDoor(pos));
                    placer.sendMessage(new StringTextComponent("Placed inside " + tile.id), placer.getUniqueID());
                    return;
                }
            }
        }
        worldIn.destroyBlock(pos, false);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
