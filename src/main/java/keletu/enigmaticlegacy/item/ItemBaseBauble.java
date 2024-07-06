package keletu.enigmaticlegacy.item;

import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ItemBaseBauble extends Item implements IBauble {

    protected final Multimap<String, AttributeModifier> attributeMap = HashMultimap.create();

    EnumRarity rare;
    public ItemBaseBauble(String name, EnumRarity rare){
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, name));
        this.setTranslationKey(name);
        this.maxStackSize = 1;

        this.rare = rare;
        this.setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return rare;
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            player.getAttributeMap().applyAttributeModifiers(attributeMap);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            player.getAttributeMap().removeAttributeModifiers(attributeMap);
        }
    }
}
