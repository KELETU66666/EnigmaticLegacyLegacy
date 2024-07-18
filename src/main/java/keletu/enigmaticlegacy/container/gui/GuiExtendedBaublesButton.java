package keletu.enigmaticlegacy.container.gui;

import baubles.client.gui.GuiPlayerExpanded;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketOpenExtendedBaublesInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiExtendedBaublesButton extends GuiButton {

	private final GuiContainer parentGui;

	public GuiExtendedBaublesButton(int buttonId, GuiContainer parentGui, int x, int y, int width, int height) {
		super(buttonId, x, parentGui.getGuiTop() + y, width, height, "button.extra");
		this.parentGui = parentGui;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean pressed = super.mousePressed(mc, mouseX - this.parentGui.getGuiLeft(), mouseY);
		if (pressed) {
			if (parentGui instanceof GuiPlayerExpanded) {
				EnigmaticLegacy.packetInstance.sendToServer(new PacketOpenExtendedBaublesInventory());
			}
		}
		return pressed;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (this.visible)
		{
			int x = this.x + this.parentGui.getGuiLeft();

			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(GuiExtraBaubles.background);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width && mouseY < this.y + this.height;
			int k = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 200);
			if (k==1) {
				this.drawTexturedModalRect(x, this.y, 200, 48, 10, 10);
			} else {
				this.drawTexturedModalRect(x, this.y, 210, 48, 10, 10);
				this.drawCenteredString(fontrenderer, I18n.format(this.displayString), x + 5, this.y + this.height, 0xffffff);
			}
			GlStateManager.popMatrix();

			this.mouseDragged(mc, mouseX, mouseY);
		}
	}
}