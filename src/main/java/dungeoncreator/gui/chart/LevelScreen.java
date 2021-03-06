package dungeoncreator.gui.chart;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LevelScreen extends Screen {
    private static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS = new ResourceLocation("textures/gui/advancements/tabs.png");
    private static final ITextComponent SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private static final ITextComponent EMPTY = new TranslationTextComponent("advancements.empty");
    private static final ITextComponent GUI_LABEL = new TranslationTextComponent("gui.advancements");
    private final Map<Tile, LevelTabGui> tabs = Maps.newLinkedHashMap();
    private LevelTabGui selectedTab;
    private boolean isScrolling;
    private static int tabPage, maxPages;

    public LevelScreen(Minecraft minecraft) {
        super(NarratorChatListener.EMPTY);
        this.minecraft = minecraft;

    }

    int gui_width = 400;
    int gui_height = 250;

    protected void init() {
        System.out.println("[LevelScreen] init");
        this.tabs.clear();
        //this.selectedTab = null;

        if (this.tabs.size() > LevelTabType.MAX_TABS) {
            int guiLeft = (this.width - gui_width) / 2;
            int guiTop = (this.height - 140) / 2;
            addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft,            guiTop - 50, 20, 20, new net.minecraft.util.text.StringTextComponent("<"), b -> tabPage = Math.max(tabPage - 1, 0       )));
            addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft + gui_width - 20, guiTop - 50, 20, 20, new net.minecraft.util.text.StringTextComponent(">"), b -> tabPage = Math.min(tabPage + 1, maxPages)));
            maxPages = this.tabs.size() / LevelTabType.MAX_TABS;
        }
    }

    public void onClose() {

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int i = (this.width - gui_width) / 2;
            int j = (this.height - gui_height) / 2;

            for(LevelTabGui advancementtabgui : this.tabs.values()) {
                if (advancementtabgui.getPage() == tabPage && advancementtabgui.isInsideTabSelector(i, j, mouseX, mouseY)) {
                    //this.clientAdvancementManager.setSelectedTab(advancementtabgui.getAdvancement(), true);
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft.gameSettings.keyBindAdvancements.matchesKey(keyCode, scanCode)) {
            this.minecraft.displayGuiScreen((Screen)null);
            this.minecraft.mouseHelper.grabMouse();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int i = (this.width - gui_width) / 2;
        int j = (this.height - gui_height) / 2;
        this.renderBackground(matrixStack);
        if (maxPages != 0) {
            net.minecraft.util.text.ITextComponent page = new net.minecraft.util.text.StringTextComponent(String.format("%d / %d", tabPage + 1, maxPages + 1));
            int width = this.font.getStringPropertyWidth(page);
            RenderSystem.disableLighting();
            this.font.func_238407_a_(matrixStack, page.func_241878_f(), i + (gui_width / 2) - (width / 2), j - 44, -1);
        }
        this.drawWindowBackground(matrixStack, mouseX, mouseY, i, j);
        this.renderWindow(matrixStack, i, j);
        this.drawWindowTooltips(matrixStack, mouseX, mouseY, i, j);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else if (this.selectedTab != null) {
                this.selectedTab.dragSelectedGui(dragX, dragY);
            }

            return true;
        }
    }

    private void drawWindowBackground(MatrixStack matrixStack, int mouseX, int mouseY, int offsetX, int offsetY) {
        LevelTabGui advancementtabgui = this.selectedTab;
        if (advancementtabgui == null) {
            //System.out.println("[drawWindowBackground] advancementtabgui is null");
            fill(matrixStack, offsetX + 9, offsetY + 18, offsetX + 9 + 234, offsetY + 18 + 113, -16777216);
            int i = offsetX + 9 + 117;
            drawCenteredString(matrixStack, this.font, EMPTY, i, offsetY + 18 + 56 - 9 / 2, -1);
            drawCenteredString(matrixStack, this.font, SAD_LABEL, i, offsetY + 18 + 113 - 9, -1);
        } else {
            //System.out.println("[drawWindowBackground] advancementtabgui is NOT null");
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(offsetX + 9), (float)(offsetY + 18), 0.0F);
            advancementtabgui.drawTabBackground(matrixStack);
            RenderSystem.popMatrix();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }
    }

    public void renderWindow(MatrixStack matrixStack, int offsetX, int offsetY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        //TODO: fix textures
        //this.minecraft.getTextureManager().bindTexture(WINDOW);
        //this.blit(matrixStack, offsetX, offsetY, 0, 0, gui_width, gui_height);
        if (this.tabs.size() >= 1) {
            this.minecraft.getTextureManager().bindTexture(TABS);

            for(LevelTabGui advancementtabgui : this.tabs.values()) {
                //if (advancementtabgui.getPage() == tabPage)
                    advancementtabgui.renderTabSelectorBackground(matrixStack, offsetX, offsetY, advancementtabgui == this.selectedTab);
            }

            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();

            for(LevelTabGui advancementtabgui1 : this.tabs.values()) {
                if (advancementtabgui1.getPage() == tabPage)
                    advancementtabgui1.drawIcon(offsetX, offsetY, this.itemRenderer);
            }

            RenderSystem.disableBlend();
        }

        this.font.func_243248_b(matrixStack, GUI_LABEL, (float)(offsetX + 8), (float)(offsetY + 6), 4210752);
    }

    private void drawWindowTooltips(MatrixStack matrixStack, int mouseX, int mouseY, int offsetX, int offsetY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            RenderSystem.pushMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.translatef((float)(offsetX + 9), (float)(offsetY + 18), 400.0F);
            this.selectedTab.drawTabTooltips(matrixStack, mouseX - offsetX - 9, mouseY - offsetY - 18, offsetX, offsetY);
            RenderSystem.disableDepthTest();
            RenderSystem.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for(LevelTabGui advancementtabgui : this.tabs.values()) {
                if (advancementtabgui.getPage() == tabPage && advancementtabgui.isInsideTabSelector(offsetX, offsetY, (double)mouseX, (double)mouseY)) {
                    this.renderTooltip(matrixStack, advancementtabgui.getTitle(), mouseX, mouseY);
                }
            }
        }

    }

    public void rootAdvancementAdded(Tile advancementIn) {
        System.out.println("[rootAdvancementAdded] " + advancementIn.getId().getPath());

        LevelTabGui advancementtabgui = LevelTabGui.create(this.minecraft, this, this.tabs.size(), advancementIn);
        if (advancementtabgui != null) {
            System.out.println("[rootAdvancementAdded] not null");
            this.tabs.put(advancementIn, advancementtabgui);
        }
        else
            System.out.println("[rootAdvancementAdded] null");

    }

    public void rootAdvancementRemoved(Tile advancementIn) {
    }

    public void nonRootAdvancementAdded(Tile advancementIn) {
        LevelTabGui advancementtabgui = this.getTab(advancementIn);
        if (advancementtabgui != null) {
            advancementtabgui.addAdvancement(advancementIn);
        }

    }

    public void setSelectedTab(@Nullable Tile advancementIn) {
        this.selectedTab = this.tabs.get(advancementIn);
        System.out.println("[setSelectedTab] is null? " + (this.selectedTab == null));
    }

    public void advancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public LevelEntryGui getAdvancementGui(Tile advancement) {
        LevelTabGui advancementtabgui = this.getTab(advancement);
        return advancementtabgui == null ? null : advancementtabgui.getLevelEntryGui(advancement);
    }

    @Nullable
    private LevelTabGui getTab(Tile advancement) {
        while(advancement.getParent() != null) {
            advancement = advancement.getParent();
        }

        return this.tabs.get(advancement);
    }
}
