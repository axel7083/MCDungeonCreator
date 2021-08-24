package dungeoncreator.gui.door;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DoorBlockScreen extends AbstractDoorBlockScreen {


    //private CommandBlockTileEntity.Mode commandBlockMode = CommandBlockTileEntity.Mode.REDSTONE;
    private boolean conditional;
    private boolean automatic;


    public DoorBlockScreen(BlockState state, BlockPos pos) {
    }

    int func_195236_i() {
        return 135;
    }

    protected void init() {
        super.init();


        this.doneButton.active = false;
    }

    public void updateGui() {
        //this.commandBlockMode = this.commandBlock.getMode();
        //this.conditional = this.commandBlock.isConditional();
        //this.automatic = this.commandBlock.isAuto();
        //this.updateTrackOutput();
        this.doneButton.active = true;
        //this.modeBtn.active = true;
    }

    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        //this.updateTrackOutput();
        this.doneButton.active = true;
        //this.modeBtn.active = true;
    }

    protected void func_195235_a(CommandBlockLogic commandBlockLogicIn) {
        //this.minecraft.getConnection().sendPacket(new CUpdateCommandBlockPacket(new BlockPos(commandBlockLogicIn.getPositionVector()), this.commandTextField.getText(), null, commandBlockLogicIn.shouldTrackOutput(), this.conditional, this.automatic));
    }

}
