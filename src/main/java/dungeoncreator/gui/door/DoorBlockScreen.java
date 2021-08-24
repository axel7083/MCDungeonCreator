package dungeoncreator.gui.door;

import dungeoncreator.models.Door;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import dungeoncreator.utils.TileUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class DoorBlockScreen extends AbstractDoorBlockScreen {


    //private CommandBlockTileEntity.Mode commandBlockMode = CommandBlockTileEntity.Mode.REDSTONE;
    private boolean conditional;
    private boolean automatic;

    private BlockPos pos;
    private PlayerEntity player;

    public DoorBlockScreen(BlockState state, BlockPos pos, PlayerEntity player) {
        this.pos = pos;
        this.player = player;
    }

    int func_195236_i() {
        return 135;
    }

    protected void init() {
        super.init();
    }

    @Override
    void save() {
        Cache cache = Cache.getInstance();
        ArrayList<String> tilesIDs = getTilesUsed();

        InGameTile tile = TileUtils.getTileWithPlayerInside(cache.worldData.objects, pos.getX(), pos.getY(), pos.getZ());

        if(tile != null) {

            Door d = tile.getDoorByBlockPos(pos);

            if(d != null) {
                d.set(
                        new int[] {parseInt(this.posXEdit.getText()), parseInt(this.posYEdit.getText()), parseInt(this.posZEdit.getText())},
                        new int[] {parseInt(this.sizeXEdit.getText()), parseInt(this.sizeYEdit.getText()), parseInt(this.sizeZEdit.getText())},
                        tilesIDs,
                        getTags(),
                        1f,
                        mode
                );
                try {
                    cache.worldData.save();
                } catch (IOException e) {
                    player.sendMessage(new StringTextComponent("ERROR the worldData could not have been saved."), player.getUniqueID());
                    e.printStackTrace();
                }
            }
            else
                player.sendMessage(new StringTextComponent("ERROR door could not be found. [??]"), player.getUniqueID());

        }
        else
            player.sendMessage(new StringTextComponent("ERROR the tile where the block door is suppose to be could not be found. [??]"), player.getUniqueID());

        super.closeScreen();

    }

    public void updateGui() {
        //this.commandBlockMode = this.commandBlock.getMode();
        //this.conditional = this.commandBlock.isConditional();
        //this.automatic = this.commandBlock.isAuto();
        //this.updateTrackOutput();
        this.saveButton.active = true;
        //this.modeBtn.active = true;
    }

    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        //this.updateTrackOutput();
        this.saveButton.active = true;
        //this.modeBtn.active = true;
    }

    protected void func_195235_a(CommandBlockLogic commandBlockLogicIn) {
        //this.minecraft.getConnection().sendPacket(new CUpdateCommandBlockPacket(new BlockPos(commandBlockLogicIn.getPositionVector()), this.commandTextField.getText(), null, commandBlockLogicIn.shouldTrackOutput(), this.conditional, this.automatic));
    }

}
