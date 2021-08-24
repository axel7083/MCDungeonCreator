package dungeoncreator.blocks;

import dungeoncreator.gui.door.DoorBlockScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DoorBlock extends Block {



    public DoorBlock() {
        super(Block.Properties.create(Material.ROCK));
    }


    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        System.out.println("onBlockActivated");
        Minecraft.getInstance().displayGuiScreen(new DoorBlockScreen(state, pos));

        return ActionResultType.SUCCESS;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!worldIn.isRemote) {
            if (placer != null) {
                //TODO: register the new door
            }

        }
    }


    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
