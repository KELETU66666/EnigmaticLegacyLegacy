package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemGemRing extends ItemBaseBauble {

    public ItemGemRing() {
        super("gem_ring", EnumRarity.UNCOMMON);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }


    @Override
    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        attributes.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(UUID.fromString("6c913e9a-0d6f-4b3b-81b9-4c82f7778b54"), EnigmaticLegacy.MODID+":luck_bonus", 1.0, 0));
    }
}
