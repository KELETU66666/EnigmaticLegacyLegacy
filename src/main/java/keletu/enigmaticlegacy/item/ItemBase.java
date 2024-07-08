package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemBase extends Item {
    EnumRarity rare;
    public ItemBase(String name, EnumRarity rare){
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, name));
        this.setTranslationKey(name);

        this.rare = rare;
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return rare;
    }
}
