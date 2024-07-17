package keletu.enigmaticlegacy.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

import java.util.List;

public class ItemCursedStone extends ItemBaseFireProof {

	public ItemCursedStone() {
		super("cursed_stone", EnumHelper.addRarity("Curses", TextFormatting.DARK_RED, "Curses"));
		this.maxStackSize = 1;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone1"));
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone2"));
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone3"));
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone4"));
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone5"));
				list.add("");
				list.add(I18n.format("tooltip.enigmaticlegacy.cursedStone6"));
			} else {
				list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
			}

			list.add("");
		}


	}