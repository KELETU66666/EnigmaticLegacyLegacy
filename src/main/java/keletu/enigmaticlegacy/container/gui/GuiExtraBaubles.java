package keletu.enigmaticlegacy.container.gui;

import baubles.client.ClientProxy;
import keletu.enigmaticlegacy.container.ContainerExtraBaubles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiExtraBaubles extends GuiContainer {
    public static final ResourceLocation background =
            new ResourceLocation("enigmaticlegacy", "textures/gui/inventory_book.png");
    private static float rotation = 0.0F;
    EntityPlayer player;

    static List<int[]> slotOverlays = null;

    public GuiExtraBaubles(EntityPlayer player) {
        super(new ContainerExtraBaubles(player.inventory, !player.getEntityWorld().isRemote, player));
        this.player = player;
        this.xSize = 213;
        this.ySize = 200;

        this.allowUserInput = true;

        if (slotOverlays == null) {
            slotOverlays = new ArrayList<int[]>();
            //CRAFTING
            slotOverlays.add(new int[]{78, 8, 53, 202});//CRAFTINGOUTPUT
            slotOverlays.add(new int[]{78, 8, 21, 202});//CRAFTING1
            slotOverlays.add(new int[]{78, 8, 37, 202});//CRAFTING2
            slotOverlays.add(new int[]{78, 8, 21, 218});//CRAFTING3
            slotOverlays.add(new int[]{78, 8, 37, 218});//CRAFTING4
            //ARMOR
            slotOverlays.add(new int[]{6, 26, 221, 19});//HELM
            slotOverlays.add(new int[]{6, 44, 221, 37});//CHEST
            slotOverlays.add(new int[]{6, 62, 239, 18});//LEGS
            slotOverlays.add(new int[]{6, 80, 239, 37});//BOOTS
            //
            slotOverlays.add(new int[]{42, 8, 239, 55});//SCROLL
            slotOverlays.add(new int[]{78, 26, 239, 73});//BOOK
            slotOverlays.add(new int[]{78, 62, 239, 91});//CURSE
            slotOverlays.add(new int[]{6, 98, 239, 109});//SPELLSTONE

            slotOverlays.add(new int[]{24, 8, 221, 55});//AMULET
            slotOverlays.add(new int[]{24, 98, 221, 73});//RING 1
            slotOverlays.add(new int[]{42, 98, 221, 73});//RING 2
            slotOverlays.add(new int[]{78, 80, 221, 127});//BACK
            slotOverlays.add(new int[]{60, 8, 239, 127});//HEAD
            slotOverlays.add(new int[]{78, 44, 221, 91});//BELT
            slotOverlays.add(new int[]{60, 98, 221, 109});//CHARM

        }
    }

    private void resetGuiLeft() {
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        ((ContainerExtraBaubles) inventorySlots).baubles.setEventBlock(false);
        resetGuiLeft();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        super.initGui();
        resetGuiLeft();

        this.buttonList.add(new GuiButton(0, this.guiLeft + 23, this.guiTop + 90, 7, 7, ""));
        this.buttonList.add(new GuiButton(1, this.guiLeft + 71, this.guiTop + 90, 7, 7, ""));
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 115, 8, 4210752);

        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor3f(1, 1, 1);
        this.mc.getTextureManager().bindTexture(background);
        this.drawTexturedModalRect(120, 69, 215, 162, 9, 9);
        this.drawTexturedModalRect(120, 79, 215, 171, 9, 9);
        this.drawTexturedModalRect(120, 89, 215, 180, 9, 9);
        this.drawTexturedModalRect(120, 99, 215, 189, 9, 9);

        this.fontRenderer.drawString("x" + this.player.getMaxHealth() / 2, 135, 69, 0x777777);
        this.fontRenderer.drawString("x" + ForgeHooks.getTotalArmorValue(this.player), 135, 79, 0x777777);
        ModifiableAttributeInstance attr = ((ModifiableAttributeInstance) this.player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED));
        this.fontRenderer.drawString((int) (attr.getAttributeValue() * 1000) + "%", 135, 90, 0x777777);
        ModifiableAttributeInstance attrDmg = ((ModifiableAttributeInstance) this.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE));
        this.fontRenderer.drawString(Math.round(attrDmg.getAttributeValue() * 100) + "%", 135, 100, 0x777777);

    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                GuiExtraBaubles.rotation += 10.0F;
                break;
            case 1:
                GuiExtraBaubles.rotation -= 10.0F;
                break;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int k = this.guiLeft;
        int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        GL11.glEnable(3042);

        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = this.inventorySlots.inventorySlots.get(i1);
            this.drawTexturedModalRect(k + slot.xPos - 1, l + slot.yPos - 1, 215, 0, 18, 18);

            if (i1 < 20 && !this.inventorySlots.inventorySlots.get(i1).getHasStack()) {
                int[] xyuv = slotOverlays.get(i1);
                this.drawTexturedModalRect(k + slot.xPos - 1, l + slot.yPos - 1, xyuv[2] - 6, xyuv[3] - 1, 16, 16);
            }
        }

        renderLiving(this.mc, k + 51, l + 80, 23);
    }

    public static void renderLiving(Minecraft mc, int x, int y, int scale) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = mc.player.renderYawOffset;
        float f3 = mc.player.rotationYaw;
        float f4 = mc.player.rotationPitch;
        float f5 = mc.player.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        mc.player.renderYawOffset = GuiExtraBaubles.rotation;
        mc.player.rotationYaw = (float) Math.atan(32 / 40.0F) * 40.0F;
        mc.player.rotationYaw = GuiExtraBaubles.rotation;
        mc.player.rotationYawHead = mc.player.rotationYaw;
        mc.player.rotationPitch = (float) Math.sin(Minecraft.getSystemTime() / 500.0F) * 3.0F;
        GlStateManager.translate(0.0F, (float) mc.player.getYOffset(), 0.0F);
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(mc.player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        mc.player.renderYawOffset = f2;
        mc.player.rotationYaw = f3;
        mc.player.rotationPitch = f4;
        mc.player.rotationYawHead = f5;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        if (par2 == ClientProxy.KEY_BAUBLES.getKeyCode()) {
            this.mc.player.closeScreen();
        } else
            super.keyTyped(par1, par2);
    }
}
