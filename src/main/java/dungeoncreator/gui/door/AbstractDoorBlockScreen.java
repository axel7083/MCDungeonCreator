package dungeoncreator.gui.door;

import com.mojang.blaze3d.matrix.MatrixStack;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractDoorBlockScreen extends Screen {

    enum DoorModes {
        MAIN_PATH,
        DEAD_END,
        SIDE_PATH,
    }

    private static final ITextComponent field_title = new StringTextComponent("WIP");


    private TextFieldWidget posXEdit;
    private TextFieldWidget posYEdit;
    private TextFieldWidget posZEdit;
    private TextFieldWidget sizeXEdit;
    private TextFieldWidget sizeYEdit;
    private TextFieldWidget sizeZEdit;


    protected Button doneButton;
    protected Button cancelButton;

    protected Button modeBtn;
    private DoorModes mode = DoorModes.MAIN_PATH;

    //private TileExtendedList list;
    //private TileExtendedList list2;

    private TileList left_list;
    private TileList right_list;


    public Pair<Integer, Integer> topLeft;


    public AbstractDoorBlockScreen() {
        super(NarratorChatListener.EMPTY);
    }

    abstract int func_195236_i();

    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        topLeft = new Pair<>((int) (this.width * 0.05), (int) (this.height * 0.05));

        this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height - topLeft.getValue() - 10, 150, 20, DialogTexts.GUI_DONE, (p_214187_1_) -> {

        }));

        this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height - topLeft.getValue() - 10, 150, 20, DialogTexts.GUI_CANCEL, (p_214186_1_) -> {
            this.closeScreen();
        }));

        // Init RelativePos and DoorSize TODO: fetch from data
        initTextField(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

        // Center this button in the top
        this.modeBtn = this.addButton(new Button(this.width / 2 - 50, topLeft.getValue() + 70, 100, 20,new StringTextComponent("Main path"), (p_214191_1_) -> {
            this.nextMode();
        }));



        // Left list (containing all tiles)
       // this.list = new TileExtendedList(Minecraft.getInstance(), this.width / 3,  topLeft.getValue(),(int) (this.height*0.80), font);
       // this.list.setLeftPos(topLeft.getKey()+20);


        // Right list (containing ONLY selected tiles)
       // this.list2 = new TileExtendedList(Minecraft.getInstance(), this.width / 3, topLeft.getValue(), (int) (this.height*0.80), font);
       // this.list2.setLeftPos( this.width - this.topLeft.getKey() - this.width / 3);
        this.left_list = new TileList(this.minecraft, 200, topLeft.getValue() + 100, this.height, new StringTextComponent("Available"));
        this.left_list.setLeftPos(this.width / 2 - 4 - 200);


        Cache cache = Cache.getInstance();

        // Fill up two time just for demo
        for(InGameTile t : cache.worldData.objects) {
            left_list.getEventListeners().add(
                    new TileList.TileEntry(this.minecraft, left_list, this, t.id, String.format("Size (%d,%d,%d)", t.sizeX, t.sizeY, t.sizeZ)));
        }
        for(InGameTile t : cache.worldData.objects) {
            left_list.getEventListeners().add(
                    new TileList.TileEntry(this.minecraft, left_list, this, t.id, String.format("Size (%d,%d,%d)", t.sizeX, t.sizeY, t.sizeZ)));
        }

        this.children.add(this.left_list);

        this.right_list = new TileList(this.minecraft, 200, topLeft.getValue() + 100, this.height, new StringTextComponent("Selected"));

        this.right_list.setLeftPos(this.width / 2 + 4);

        right_list.getEventListeners().add(new TileList.TileEntry(this.minecraft, right_list, this,"", ""));

        this.children.add(this.right_list);
    }

    private void initTextField(BlockPos relativePos, BlockPos doorSize) {

        // Setup door relative pos
        this.posXEdit = new TextFieldWidget(this.font, this.width / 2 - 120, this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.x"));
        this.posXEdit.setMaxStringLength(15);
        this.posXEdit.setText(Integer.toString(relativePos.getX()));
        this.children.add(this.posXEdit);
        this.posYEdit = new TextFieldWidget(this.font, this.width / 2 - 40, this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.y"));
        this.posYEdit.setMaxStringLength(15);
        this.posYEdit.setText(Integer.toString(relativePos.getY()));
        this.children.add(this.posYEdit);
        this.posZEdit = new TextFieldWidget(this.font, this.width / 2 + 40, this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.z"));
        this.posZEdit.setMaxStringLength(15);
        this.posZEdit.setText(Integer.toString(relativePos.getZ()));
        this.children.add(this.posZEdit);

        // Setup door size
        this.sizeXEdit = new TextFieldWidget(this.font, this.width / 2 - 120, this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.x"));
        this.sizeXEdit.setMaxStringLength(15);
        this.sizeXEdit.setText(Integer.toString(doorSize.getX()));
        this.children.add(this.sizeXEdit);
        this.sizeYEdit = new TextFieldWidget(this.font, this.width / 2 - 40, this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.y"));
        this.sizeYEdit.setMaxStringLength(15);
        this.sizeYEdit.setText(Integer.toString(doorSize.getY()));
        this.children.add(this.sizeYEdit);
        this.sizeZEdit = new TextFieldWidget(this.font, this.width / 2 + 40, this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.z"));
        this.sizeZEdit.setMaxStringLength(15);
        this.sizeZEdit.setText(Integer.toString(doorSize.getZ()));
        this.children.add(this.sizeZEdit);
    }

    public void tick() {
        this.posXEdit.tick();
        this.posYEdit.tick();
        this.posZEdit.tick();
        this.sizeXEdit.tick();
        this.sizeYEdit.tick();
        this.sizeZEdit.tick();
    }

    private void nextMode() {
        switch (mode) {
            case MAIN_PATH:
                mode = DoorModes.DEAD_END;
                this.modeBtn.setMessage(new StringTextComponent("Dead Ends"));
                break;
            case DEAD_END:
                mode = DoorModes.SIDE_PATH;
                this.modeBtn.setMessage(new StringTextComponent("Side path"));
                break;
            case SIDE_PATH:
                mode = DoorModes.MAIN_PATH;
                this.modeBtn.setMessage(new StringTextComponent("Main path"));
                break;
        }
    }

    public void resize(Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    protected abstract void func_195235_a(CommandBlockLogic commandBlockLogicIn);

    public void closeScreen() {
        this.minecraft.displayGuiScreen((Screen)null);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        System.out.println("keyPressed");
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            System.out.println("1");
            return true;
        } else if (keyCode != 257 && keyCode != 335) {
            System.out.println("2");
            return false;
        } else {
            System.out.println("3");
            return true;
        }
    }


    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        this.renderDirtBackground(0);

       // this.list.render(matrixStack, mouseX, mouseY, partialTicks);
        //this.list2.render(matrixStack, mouseX, mouseY, partialTicks);
        this.left_list.render(matrixStack, mouseX, mouseY, partialTicks);
        this.right_list.render(matrixStack, mouseX, mouseY, partialTicks);

        drawString(matrixStack, this.font, field_title,topLeft.getKey() ,topLeft.getValue(), 16777215);
        //drawCenteredString(matrixStack, this.font, field_title, topLeft.getKey(), topLeft.getValue(), 16777215);

        // Render text field
        drawString(matrixStack, this.font, new StringTextComponent("Relative position"), this.width / 2 - 120, this.topLeft.getValue() - 10, 10526880);
        this.posXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.posYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.posZEdit.render(matrixStack, mouseX, mouseY, partialTicks);


        drawString(matrixStack, this.font, new StringTextComponent("Door size"), this.width / 2 - 120, this.topLeft.getValue() + 30, 10526880);
        this.sizeXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.sizeYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.sizeZEdit.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
