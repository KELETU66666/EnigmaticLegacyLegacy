package keletu.enigmaticlegacy.util.compat;

import static keletu.enigmaticlegacy.event.ELEvents.hasCursed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.items.baubles.BaubleEnderTiara;

public class CompatTrinketEvent {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void addEnderTiaraInformation(ItemTooltipEvent event){
        if(event.getEntityPlayer() == null)
            return;

        if(event.getItemStack().getItem() instanceof BaubleEnderTiara && hasCursed(event.getEntityPlayer()))
        {
            event.getToolTip().add("");

            if (GuiScreen.isShiftKeyDown()) {
                if (Minecraft.getMinecraft().player != null && hasCursed(Minecraft.getMinecraft().player)) {
                    event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.compatXat1"));
                    event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.compatXat2"));
                }
            } else {
                event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
            }
        }
    }
}
