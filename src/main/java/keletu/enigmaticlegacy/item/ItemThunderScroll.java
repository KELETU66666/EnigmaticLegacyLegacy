package keletu.enigmaticlegacy.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemThunderScroll extends ItemScrollBauble {

    public ItemThunderScroll() {
        super("thunder_scroll", EnumRarity.EPIC);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add("");
            list.add(I18n.format("tooltip.enigmaticaddons.thunderScroll1"));
            list.add(I18n.format("tooltip.enigmaticaddons.thunderScroll2"));
            list.add(I18n.format("tooltip.enigmaticaddons.thunderScroll3"));
            list.add(I18n.format("tooltip.enigmaticaddons.thunderScroll4"));
            list.add(I18n.format("tooltip.enigmaticaddons.thunderScroll5"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    public static float modify(EntityLivingBase target, float damage) {
        if (target.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR) != null) {
            double value = target.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue();
            if (value > 0) {
                double factor = 1.0 - Math.min(0.0375 * value, 0.75);
                damage = (float) (damage / Math.sqrt(factor));
            }
        }
        return damage;
    }
}