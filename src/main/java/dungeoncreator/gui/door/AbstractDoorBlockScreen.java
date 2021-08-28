package dungeoncreator.gui.door;

import com.mojang.blaze3d.matrix.MatrixStack;
import dungeoncreator.models.InGameTile;
import dungeoncreator.utils.Cache;
import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractDoorBlockScreen extends Screen implements TileList.Events {

    public enum DoorModes {
        MAIN_PATH,
        DEAD_END,
        SIDE_PATH,
    }

    public enum TagsModes {
        NONE,
        ENTER,
        EXIT,
    }

    private static final ITextComponent field_mode = new StringTextComponent("Mode");
    private static final ITextComponent field_name = new StringTextComponent("Door Name");


    protected TextFieldWidget posXEdit;
    protected TextFieldWidget posYEdit;
    protected TextFieldWidget posZEdit;
    protected TextFieldWidget sizeXEdit;
    protected TextFieldWidget sizeYEdit;
    protected TextFieldWidget sizeZEdit;

    protected TextFieldWidget nameEdit;


    protected Button saveButton;
    protected Button cancelButton;

    protected Button modeBtn;
    protected Button tagsBtn;
    protected DoorModes doorMode = DoorModes.MAIN_PATH;
    protected TagsModes tagsMode = TagsModes.NONE;

    private TileList left_list;
    private TileList right_list;

    public Pair<Integer, Integer> topLeft;

    @Override
    public void onClick(TileList.TileEntry tileEntry) {
        // Exchanging from left list to right list the clicked tile Entry
        if(left_list.getEventListeners().contains(tileEntry)) {
            left_list.getEventListeners().remove(tileEntry);
            right_list.getEventListeners().add(new TileList.TileEntry(this.minecraft, right_list, this, tileEntry.tile, this));
        }
        else
        {
            right_list.getEventListeners().remove(tileEntry);
            left_list.getEventListeners().add(new TileList.TileEntry(this.minecraft, left_list, this, tileEntry.tile, this));
        }
    }

    public AbstractDoorBlockScreen() {
        super(NarratorChatListener.EMPTY);
    }

    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);

        topLeft = new Pair<>((int) (this.width * 0.05), (int) (this.height * 0.05));

        this.saveButton = this.addButton(new Button(this.width - 100 - topLeft.getKey(), this.height - topLeft.getValue() - 10, 100, 20, DialogTexts.GUI_DONE, (p_214187_1_) -> {
            save();
        }));

        saveButton.active = true;

        this.cancelButton = this.addButton(new Button(this.width - 210 - topLeft.getKey(), this.height - topLeft.getValue() - 10, 100, 20, DialogTexts.GUI_CANCEL, (p_214186_1_) -> {
            this.closeScreen();
        }));

        // Init RelativePos and DoorSize TODO: fetch from data
        initTextField(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0));

        // Center this button in the top
        this.modeBtn = this.addButton(new Button(this.width - topLeft.getKey() - 120, topLeft.getValue(), 120, 20,new StringTextComponent("Main path"), (p_214191_1_) -> {
            this.nextMode();
        }));

        this.tagsBtn = this.addButton(new Button(this.width - topLeft.getKey() - 100, topLeft.getValue() + 100, 100, 20,new StringTextComponent("None"), (p_214191_1_) -> {
            this.nextTags();
        }));

        this.left_list = new TileList(this.minecraft, 150, topLeft.getValue() + 100, this.height, new StringTextComponent("Available"));
        this.left_list.setLeftPos(this.topLeft.getKey());


        // Create left list (containing selected tiles)
        this.right_list = new TileList(this.minecraft, 150, topLeft.getValue() + 100, this.height, new StringTextComponent("Selected"));
        this.right_list.setLeftPos(this.topLeft.getKey() + 160);


        load();
        this.children.add(this.left_list);
        this.children.add(this.right_list);
    }

    protected String getTags() {

        if(doorMode.equals(DoorModes.MAIN_PATH))
            switch (tagsMode) {
                case NONE:
                    return "";
                case ENTER:
                    return "enter";
                case EXIT:
                    return "exit";
            }

        return "deadend";
    }


    protected void setTileUsed(ArrayList<String> tilesIds) {
        if(tilesIds == null)
            tilesIds = new ArrayList<>();

        Cache cache = Cache.getInstance();

        for(InGameTile t : cache.worldData.objects) {
            if(tilesIds.contains(t.id))
                right_list.getEventListeners().add(
                        new TileList.TileEntry(this.minecraft, right_list, this, t, this));
            else
                left_list.getEventListeners().add(
                        new TileList.TileEntry(this.minecraft, left_list, this, t, this));
        }
    }

    public ArrayList<String> getTilesUsed() {
        ArrayList<String> tilesIDs = new ArrayList<>();
        for(TileList.TileEntry entry : right_list.getEventListeners()) {
            tilesIDs.add(entry.tile.id);
        }
        return tilesIDs;
    }

    abstract void load();
    abstract void save();

    protected float parseFloat(String v) {
        try {
            return Float.parseFloat(v);
        } catch (NumberFormatException numberformatexception) {
            return 1.0F;
        }
    }

    protected int parseInt(String v) {
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException numberformatexception) {
            return 1;
        }
    }

    private void initTextField(BlockPos relativePos, BlockPos doorSize) {

        // Setup door relative pos
        this.posXEdit = new TextFieldWidget(this.font, this.topLeft.getKey(), this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.x"));
        this.posXEdit.setMaxStringLength(15);
        this.posXEdit.setText(Integer.toString(relativePos.getX()));
        this.children.add(this.posXEdit);
        this.posYEdit = new TextFieldWidget(this.font, this.topLeft.getKey()+80, this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.y"));
        this.posYEdit.setMaxStringLength(15);
        this.posYEdit.setText(Integer.toString(relativePos.getY()));
        this.children.add(this.posYEdit);
        this.posZEdit = new TextFieldWidget(this.font, this.topLeft.getKey()+2*80, this.topLeft.getValue(), 80, 20, new TranslationTextComponent("structure_block.position.z"));
        this.posZEdit.setMaxStringLength(15);
        this.posZEdit.setText(Integer.toString(relativePos.getZ()));
        this.children.add(this.posZEdit);

        // Setup door size
        this.sizeXEdit = new TextFieldWidget(this.font, this.topLeft.getKey(), this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.x"));
        this.sizeXEdit.setMaxStringLength(15);
        this.sizeXEdit.setText(Integer.toString(doorSize.getX()));
        this.children.add(this.sizeXEdit);
        this.sizeYEdit = new TextFieldWidget(this.font, this.topLeft.getKey()+80, this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.y"));
        this.sizeYEdit.setMaxStringLength(15);
        this.sizeYEdit.setText(Integer.toString(doorSize.getY()));
        this.children.add(this.sizeYEdit);
        this.sizeZEdit = new TextFieldWidget(this.font, this.topLeft.getKey()+2*80, this.topLeft.getValue() + 40, 80, 20, new TranslationTextComponent("structure_block.size.z"));
        this.sizeZEdit.setMaxStringLength(15);
        this.sizeZEdit.setText(Integer.toString(doorSize.getZ()));
        this.children.add(this.sizeZEdit);

        this.nameEdit = new TextFieldWidget(this.font, this.width - this.topLeft.getKey() - 120, this.topLeft.getValue() + 40, 120, 20, new StringTextComponent("name"));
        this.nameEdit.setMaxStringLength(30);
        this.nameEdit.setText("");
        this.children.add(this.nameEdit);
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
        switch (doorMode) {
            case MAIN_PATH:
                doorMode = DoorModes.DEAD_END;
                this.tagsBtn.visible = false; // Since all dead end MUST have "deadend" tag
                this.modeBtn.setMessage(new StringTextComponent("Dead Ends"));
                break;
            case DEAD_END:
                doorMode = DoorModes.SIDE_PATH;
                this.tagsBtn.visible = false; // Since all side-path MUST have "deadend" tag
                this.modeBtn.setMessage(new StringTextComponent("Side path"));
                break;
            case SIDE_PATH:
                doorMode = DoorModes.MAIN_PATH;
                this.tagsBtn.visible = true; // can be "Enter" Or "Exit" or nothing
                this.modeBtn.setMessage(new StringTextComponent("Main path"));
                break;
        }
    }

    private void nextTags() {
        switch (tagsMode) {
            case NONE:
                tagsMode = TagsModes.ENTER;
                this.tagsBtn.setMessage(new StringTextComponent("Enter"));
                break;
            case ENTER:
                tagsMode = TagsModes.EXIT;
                this.tagsBtn.setMessage(new StringTextComponent("Exit"));
                break;
            case EXIT:
                tagsMode = TagsModes.NONE;
                this.tagsBtn.setMessage(new StringTextComponent("None"));
                break;
        }
    }

    public void resize(Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

    public void onClose() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

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

        // If we are an enter AND the mode is MAIN_PATH we do not need to specify a tile.
        if (!tagsMode.equals(TagsModes.ENTER) || !doorMode.equals(DoorModes.MAIN_PATH)) {
            // Render the lists
            this.left_list.render(matrixStack, mouseX, mouseY, partialTicks);
            this.right_list.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        // Draw Mode title
        drawString(matrixStack, this.font, field_mode,this.width - topLeft.getKey() - font.getStringWidth(field_mode.getString()),topLeft.getValue() - 10, 10526880);

        // Draw Name title
        drawString(matrixStack, this.font, field_name,this.width - topLeft.getKey() - font.getStringWidth(field_name.getString()),topLeft.getValue()+ 30, 10526880);
        this.nameEdit.render(matrixStack, mouseX, mouseY, partialTicks);

        // Render text field
        drawString(matrixStack, this.font, new StringTextComponent("Relative position"), this.topLeft.getKey(), this.topLeft.getValue() - 10, 10526880);
        this.posXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.posYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.posZEdit.render(matrixStack, mouseX, mouseY, partialTicks);

        drawString(matrixStack, this.font, new StringTextComponent("Door size"), this.topLeft.getKey(), this.topLeft.getValue() + 30, 10526880);
        this.sizeXEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.sizeYEdit.render(matrixStack, mouseX, mouseY, partialTicks);
        this.sizeZEdit.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
