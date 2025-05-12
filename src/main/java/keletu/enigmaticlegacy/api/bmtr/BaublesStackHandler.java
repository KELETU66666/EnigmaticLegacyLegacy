package keletu.enigmaticlegacy.api.bmtr;

import baubles.api.cap.BaublesContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class BaublesStackHandler extends BaublesContainer {
	
	public BaublesStackHandler() {
		super();
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		setSize(7 + 7);
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, new ItemStack(itemTags));
            }
        }
        onLoad();
	}

}