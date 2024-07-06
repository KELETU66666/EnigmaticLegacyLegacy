package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemGemRing extends ItemBaseBauble {

    public ItemGemRing() {
        super("gem_ring", EnumRarity.UNCOMMON);

        this.attributeMap.put(SharedMonsterAttributes.LUCK.getName(), new AttributeModifier(UUID.fromString("6c913e9a-0d6f-4b3b-81b9-4c82f7778b52"), EnigmaticLegacy.MODID+":luck_bonus", 1.0, 0));
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }
}
