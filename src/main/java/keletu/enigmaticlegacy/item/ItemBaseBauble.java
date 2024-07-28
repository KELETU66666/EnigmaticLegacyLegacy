package keletu.enigmaticlegacy.item;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBaseBauble extends ItemBase implements IBauble {
    public ItemBaseBauble(String name, EnumRarity rare){
        super(name, rare);
        this.maxStackSize = 1;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return BaublesApi.isBaubleEquipped((EntityPlayer) player, this) == -1;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        onEquippedOrLoadedIntoWorld(stack, player);
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase player) {
        if (player != null) {
            onEquippedOrLoadedIntoWorld(stack, player);
        }
    }

    public void onEquippedOrLoadedIntoWorld(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().applyAttributeModifiers(attributes);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase player) {
        if (!player.world.isRemote) {
            Multimap<String, AttributeModifier> attributes = HashMultimap.create();
            fillModifiers(attributes, stack);
            player.getAttributeMap().removeAttributeModifiers(attributes);
        }
    }

    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack){};
}
