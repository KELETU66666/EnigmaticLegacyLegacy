package keletu.enigmaticlegacy.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemIngotWitherite extends ItemBaseFireProof {

    public ItemIngotWitherite() {
        super("witherite_ingot", EnumRarity.UNCOMMON);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.enigmaticlegacy.witherite1"));
        tooltip.add(I18n.format("tooltip.enigmaticlegacy.witherite2"));
        tooltip.add(I18n.format("tooltip.enigmaticlegacy.witherite3"));
    }
}