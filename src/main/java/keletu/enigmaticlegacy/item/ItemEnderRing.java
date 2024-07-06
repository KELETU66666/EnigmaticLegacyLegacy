package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderRing extends ItemBaseBauble{
    public ItemEnderRing() {
        super("ender_ring", EnumRarity.UNCOMMON);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.enderRing1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderRing2"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        try {
            list.add("");
            list.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.enigmaticlegacy.currentKeybind") + KeyBinding.getDisplayString("key.enderRing").get().toUpperCase());
        } catch (NullPointerException ex) {
            // Just don't do it lol
        }

    }
    
    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }
}
