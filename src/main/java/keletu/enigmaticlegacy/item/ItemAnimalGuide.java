package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.api.ExtendedBaubleType;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import static keletu.enigmaticlegacy.event.SuperpositionHandler.hasCursed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAnimalGuide extends ItemBase implements IExtendedBauble {
    public ItemAnimalGuide() {
        super("animal_guide", EnumRarity.UNCOMMON);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.animalGuide1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.animalGuide2"));

            if (Minecraft.getMinecraft().player != null && hasCursed(Minecraft.getMinecraft().player)) {
                list.add("");
                list.add(I18n.format("tooltip.enigmaticlegacy.animalGuide3"));
                list.add(I18n.format("tooltip.enigmaticlegacy.animalGuide4"));
            }
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

    }
    @Override
    public ExtendedBaubleType getBaubleType(ItemStack itemstack) {
        return ExtendedBaubleType.BOOK;
    }
}
