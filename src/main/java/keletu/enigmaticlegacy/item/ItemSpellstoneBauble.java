package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.util.interfaces.ISpellstone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public abstract class ItemSpellstoneBauble extends ItemBase implements IBauble, ISpellstone {


    public List<String> immunityList = new ArrayList<String>();
    public HashMap<String, Supplier<Float>> resistanceList = new HashMap<String, Supplier<Float>>();

    public ItemSpellstoneBauble(String props, EnumRarity rare) {
        super(props, rare);
        this.maxStackSize = 1;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) player);

        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack equipped = baubles.getStackInSlot(slot);
            if (!equipped.isEmpty() && equipped.getItem() instanceof ISpellstone) {
                return false;
            }
        }

        return true;
    }

    public boolean isResistantTo(String damageType) {
        return this.resistanceList.containsKey(damageType);
    }

    public boolean isImmuneTo(String damageType) {
        return this.immunityList.contains(damageType);
    }

    public Supplier<Float> getResistanceModifier(String damageType) {
        return this.resistanceList.get(damageType);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
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

    abstract void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack);
}