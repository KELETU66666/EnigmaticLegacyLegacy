package keletu.enigmaticlegacy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ItemLostEngine extends ItemSpellstoneBauble {

    public ItemLostEngine() {
        super("lost_engine", EnumRarity.RARE);
        this.immunityList.add(DamageSource.FALL.damageType);
        this.immunityList.add("explosion");
        this.immunityList.add("explosion.player");
        this.immunityList.add(DamageSource.CACTUS.damageType);
        Supplier<Float> magicVulnerabilitySupplier = () -> (float) EnigmaticConfigs.lostEngineVulnerabilityModifier;
        this.resistanceList.put(DamageSource.MAGIC.damageType, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageSource.WITHER.damageType, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageSource.DRAGON_BREATH.damageType, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageSource.LIGHTNING_BOLT.damageType, () -> 2.0F);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine1"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine2"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngineCooldown", 0F));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine3"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine4"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine5"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine6"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine7", EnigmaticConfigs.lostEngineCritModifier + "%%"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine8"));
            list.add(I18n.format("tooltip.enigmaticaddons.lostEngine9"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        //try {
        //    list.add("");
        //    list.add(I18n.format("tooltip.enigmaticlegacy.currentKeybind", TextFormatting.LIGHT_PURPLE, KeyMapping.createNameSupplier("key.spellstoneAbility").get().getString().toUpperCase()));
        //} catch (NullPointerException ignored) {
        //}
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)  {
        Multimap<String, AttributeModifier> attributes = HashMultimap.create();
        return attributes;
    }

    @Override
    void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        //attributes.put(ForgeMod.ENTITY_GRAVITY.get(), new AttributeModifier(UUID.fromString("299331B9-B0F5-ED8E-EF2F-0DB551E79827"), "Gravity boost", gravityModifier.getValue(), 2));
        attributes.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("125BF64C-96A5-5940-57B2-96625C975B7C"), "Speed boost", EnigmaticConfigs.lostEngineSpeedModifier, 2));
        attributes.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(UUID.fromString("EF576F66-2F15-802E-1A98-37F4A8A2D651"), "Armor Toughness boost", EnigmaticConfigs.lostEngineToughnessModifier, 0));
        attributes.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(UUID.fromString("323423C8-E3F4-0AE3-4833-16F26078A927"), "Knockback Resistance boost", EnigmaticConfigs.lostEngineKRModifier, 0));
    }
}