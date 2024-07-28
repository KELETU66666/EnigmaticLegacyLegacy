package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemIronRing extends ItemBaseBauble {

    public ItemIronRing() {
        super("iron_ring", EnumRarity.COMMON);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(UUID.fromString("371929FC-4CBC-11E8-842F-0EE5F89F718B"), "Armor bonus", 1.0, 0));
    }
}
