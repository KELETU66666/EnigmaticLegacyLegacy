package keletu.enigmaticlegacy.item;

import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBaseBauble extends ItemBase implements IBauble {

    protected final Multimap<String, AttributeModifier> attributeMap = HashMultimap.create();
    public ItemBaseBauble(String name, EnumRarity rare){
        super(name, rare);
        this.maxStackSize = 1;
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
