package keletu.enigmaticlegacy.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemEnchantmentTransposer extends ItemBase {

    public ItemEnchantmentTransposer() {
        super("enchantment_transposer", EnumRarity.UNCOMMON);
        this.maxStackSize = 1;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.enchantmentTransposer1"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.enchantmentTransposer2"));
            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.enchantmentTransposer3"));
        } else {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

    }
}