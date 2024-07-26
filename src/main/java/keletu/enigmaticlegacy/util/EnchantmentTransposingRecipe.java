package keletu.enigmaticlegacy.util;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Special recipe type for transposing enchantments.
 *
 * @author Integral
 */

public class EnchantmentTransposingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();
        ItemStack transposer = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkedItemStack = inv.getStackInSlot(i);

            if (!checkedItemStack.isEmpty()) {
                if (checkedItemStack.getItem() == EnigmaticLegacy.enchantmentTransposer) {
                    if (transposer == null) {
                        transposer = checkedItemStack.copy();
                    } else
                        return ItemStack.EMPTY;
                } else {
                    stackList.add(checkedItemStack);
                }

            }

        }

        if (transposer != null && stackList.size() == 1 && stackList.get(0).isItemEnchanted()) {
            ItemStack enchanted = stackList.get(0).copy();
            NBTTagList enchantmentNBT = enchanted.getEnchantmentTagList();

            ItemStack returned = new ItemStack(Items.ENCHANTED_BOOK);
            if (returned.getTagCompound() != null)
                returned.getTagCompound().setTag("StoredEnchantments", enchantmentNBT);
            else{
                NBTTagCompound tag = new NBTTagCompound();
                tag.setTag("StoredEnchantments", enchantmentNBT);
                returned.setTagCompound(tag);
            }
            return returned;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();
        ItemStack transposer = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkedItemStack = inv.getStackInSlot(i);

            if (!checkedItemStack.isEmpty()) {
                if (checkedItemStack.getItem() == EnigmaticLegacy.enchantmentTransposer) {
                    if (transposer == null) {
                        transposer = checkedItemStack.copy();
                    } else
                        return false;
                } else {
                    stackList.add(checkedItemStack);
                }

            }

        }

        if (transposer != null && stackList.size() == 1 && stackList.get(0).isItemEnchanted())
            return true;

        return false;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (item.getItem() != EnigmaticLegacy.enchantmentTransposer && item.isItemEnchanted()) {
                ItemStack returned = item.copy();
                if (returned.getTagCompound() != null) {
                    NBTTagCompound nbt = returned.getTagCompound();

                    nbt.removeTag("ench");
                    //returned.setTag(nbt);

                    nonnulllist.set(i, returned);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

}