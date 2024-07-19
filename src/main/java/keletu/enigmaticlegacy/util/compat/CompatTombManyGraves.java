package keletu.enigmaticlegacy.util.compat;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import keletu.enigmaticlegacy.api.ExtendedBaublesApi;
import keletu.enigmaticlegacy.api.inv.ExtendedBaublesInventoryWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CompatTombManyGraves  extends AbstractSpecialInventory {
    @Override
    public String getUniqueIdentifier() {
        return "EnigmaticInventory";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isOverwritable() {
        return true;
    }

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true;
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {
        return SpecialInventoryHelper.getTagListFromIInventory(ExtendedBaublesApi.getBaubles(player));
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagList && player instanceof EntityPlayerMP){
            TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
            ExtendedBaublesInventoryWrapper inventoryGC = (ExtendedBaublesInventoryWrapper) ExtendedBaublesApi.getBaubles(player);

            for (int i=0; i<graveItems.getSizeInventory(); i++){
                ItemStack graveItem = graveItems.getStackInSlot(i);

                if (!graveItem.isEmpty()){
                    ItemStack playerItem = inventoryGC.getStackInSlot(i).copy();

                    if (playerItem.isEmpty()){
                        // No problem, just put the grave item in!
                        inventoryGC.setInventorySlotContents(i, graveItem);
                    } else if (shouldForce){
                        // Slot is blocked, but we're forcing it
                        inventoryGC.setInventorySlotContents(i, graveItem);
                        SpecialInventoryHelper.dropItem(player, playerItem);
                    } else {
                        // Slot is blocked, but not forcing
                        SpecialInventoryHelper.dropItem(player, graveItem);
                    }
                }
            }
        }

    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(NBTBase compound) {
        if (compound instanceof NBTTagList){
            return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
        } else {
            return new ArrayList<ItemStack>();
        }
    }

    @Override
    public String getInventoryDisplayNameForGui() {
        return "EnigmaticLegacy²";
    }

    @Override
    public int getInventoryDisplayNameColorForGui() {
        return 0;
    }
}
