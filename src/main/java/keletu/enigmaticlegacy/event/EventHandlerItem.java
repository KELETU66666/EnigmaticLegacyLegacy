package keletu.enigmaticlegacy.event;

import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.container.gui.GuiEnderChestInventoryButton;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class EventHandlerItem {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Minecraft.getMinecraft().player == null)
            return;

        if (event.getGui() instanceof GuiInventory) {
            GuiContainer gui = (GuiContainer) event.getGui();

            if (hasCursed(Minecraft.getMinecraft().player) || hasEnderRing(Minecraft.getMinecraft().player))
                event.getButtonList().add(new GuiEnderChestInventoryButton(56, gui, 127 + ELConfigs.xiconOffsetEnderChest, 61 + ELConfigs.yiconOffsetEnderChest, 20, 18));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        TextFormatting format = TextFormatting.DARK_RED;
        EntityPlayer player = Minecraft.getMinecraft().player;

        for (ResourceLocation rl : ELConfigs.eldritchItemList) {
            if (event.getItemStack().getItem() == ForgeRegistries.ITEMS.getValue(rl)) {
                if (player != null) {
                    format = isTheWorthyOne(Minecraft.getMinecraft().player) ? TextFormatting.GOLD : TextFormatting.DARK_RED;
                }

                event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly1"));
                event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly2"));
                event.getToolTip().add(I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly3"));
                event.getToolTip().add("");
                event.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.enigmaticlegacy.worthyOnesOnly4") + format + " " + SuperpositionHandler.getSufferingTime(player));
            }
        }
    }

}