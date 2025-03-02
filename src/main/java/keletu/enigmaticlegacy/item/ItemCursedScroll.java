package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticConfigs;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.getCurseAmount;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCursedScroll extends ItemScrollBauble {

    public ItemCursedScroll() {
        super("cursed_scroll", EnumRarity.EPIC);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + EnigmaticConfigs.cursedScrollDamageBoost * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + EnigmaticConfigs.cursedScrollMiningBoost * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + EnigmaticConfigs.cursedScrollRegenBoost * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll3"));

            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.cursed_scroll4"));
            list.add(I18n.format("tooltip.enigmaticlegacy.cursed_scroll5"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        if (Minecraft.getMinecraft().player != null)
            if (hasCursed(Minecraft.getMinecraft().player)) {
                int curses = getCurseAmount(Minecraft.getMinecraft().player);

                list.add("");
                list.add(I18n.format("tooltip.enigmaticlegacy.cursed_scroll6"));
                list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + (EnigmaticConfigs.cursedScrollDamageBoost * 100 * curses) + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll7"));
                list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + (EnigmaticConfigs.cursedScrollMiningBoost * 100 * curses) + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll8"));
                list.add(I18n.format("tooltip.enigmaticlegacy.add") + TextFormatting.GOLD + (EnigmaticConfigs.cursedScrollRegenBoost * 100 * curses) + "%" + I18n.format("tooltip.enigmaticlegacy.cursed_scroll9"));

            }
    }
}