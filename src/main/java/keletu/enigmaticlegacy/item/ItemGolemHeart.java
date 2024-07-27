package keletu.enigmaticlegacy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ItemGolemHeart extends ItemSpellstoneBauble {

    //private static final ResourceLocation TEXTURE = new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/armor/dark_armor.png");
    public Object model;

    public Multimap<String, AttributeModifier> attributesDefault = HashMultimap.create();
    public Multimap<String, AttributeModifier> attributesNoArmor = HashMultimap.create();

    public ItemGolemHeart() {
        super("golem_heart", EnumRarity.UNCOMMON);

        this.immunityList.add(DamageSource.CACTUS.damageType);
        this.immunityList.add(DamageSource.CRAMMING.damageType);
        this.immunityList.add(DamageSource.IN_WALL.damageType);
        this.immunityList.add(DamageSource.FALLING_BLOCK.damageType);

        Supplier<Float> meleeResistanceSupplier = () -> meleeResistance;
        Supplier<Float> explosionResistanceSupplier = () -> explosionResistance;
        Supplier<Float> magicVulnerabilitySupplier = () -> (float) vulnerabilityModifier;

        this.resistanceList.put(DamageSource.GENERIC.damageType, meleeResistanceSupplier);
        this.resistanceList.put("mob", meleeResistanceSupplier);
        this.resistanceList.put("player", meleeResistanceSupplier);
        this.resistanceList.put("explosion", explosionResistanceSupplier);
        this.resistanceList.put("explosion.player", explosionResistanceSupplier);

        this.resistanceList.put(DamageSource.MAGIC.damageType, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageSource.WITHER.damageType, magicVulnerabilitySupplier);
        this.resistanceList.put(DamageSource.DRAGON_BREATH.damageType, magicVulnerabilitySupplier);
    }

    public void fillModifiers(Multimap<String, AttributeModifier> attributes, ItemStack stack) {
        this.attributesDefault.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(UUID.fromString("15faf191-bf21-4654-b359-cc1f4f1243bf"), "GolemHeart DAB", defaultArmorBonus, 0));
        this.attributesDefault.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(UUID.fromString("10faf191-bf21-4554-b359-cc1f4f1233bf"), "GolemHeart KR", knockbackResistance, 0));

        this.attributesNoArmor.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(UUID.fromString("14faf191-bf23-4654-b359-cc1f4f1243bf"), "GolemHeart SAB", superArmorBonus, 0));
        this.attributesNoArmor.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(UUID.fromString("11faf181-bf23-4354-b359-cc1f5f1253bf"), "GolemHeart STB", superArmorToughnessBonus, 0));
        this.attributesNoArmor.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(UUID.fromString("12faf181-bf21-4554-b359-cc1f4f1254bf"), "GolemHeart KR", knockbackResistance, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart2"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeartCooldown"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart3"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart4") + TextFormatting.GOLD + (int) defaultArmorBonus + I18n.format("tooltip.enigmaticlegacy.golemHeart4_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart6") + TextFormatting.GOLD + (int) superArmorBonus + I18n.format("tooltip.enigmaticlegacy.golemHeart6_1") + (int) superArmorToughnessBonus + I18n.format("tooltip.enigmaticlegacy.golemHeart6_2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart7") + TextFormatting.GOLD + explosionResistance * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.golemHeart7_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart8") + TextFormatting.GOLD + meleeResistance * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.golemHeart8_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart9") + TextFormatting.GOLD + knockbackResistance * 100 + "%" + I18n.format("tooltip.enigmaticlegacy.golemHeart9_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart10"));
            list.add(I18n.format("tooltip.enigmaticlegacy.golemHeart11"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            AbstractAttributeMap map = player.getAttributeMap();
            map.removeAttributeModifiers(this.attributesDefault);
            map.removeAttributeModifiers(this.attributesNoArmor);
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        super.onWornTick(stack, living);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            AbstractAttributeMap map = player.getAttributeMap();

            if (SuperpositionHandler.hasAnyArmor(player)) {
                // Removes attributes
                map.removeAttributeModifiers(this.attributesNoArmor);

                // Applies new attributes
                map.applyAttributeModifiers(this.attributesDefault);
            } else {
                // Removes attributes
                map.removeAttributeModifiers(this.attributesDefault);

                // Applies new attributes
                map.applyAttributeModifiers(this.attributesNoArmor);
            }

        }
    }
	
	/*
	 @Override
	 @OnlyIn(Dist.CLIENT)
	 protected DarkArmorModel getModel() {

		 if (this.model == null) {
			 this.model = new DarkArmorModel(EquipmentSlotType.MAINHAND);
		 }

		boolean shouldUpdate = false;

		for (AnchorType anchorType : AnchorType.values()) {

			float offsetX = 0F, offsetY = 0F, offsetZ = 0F;

			offsetX = JsonConfigHandler.getFloat(anchorType.name + "_x");
			offsetY = JsonConfigHandler.getFloat(anchorType.name + "_y");
			offsetZ = JsonConfigHandler.getFloat(anchorType.name + "_z");

			if (SpecialArmorModelRenderer.offsetMap.getOrDefault(anchorType.name + "_x", -32768F) != offsetX || SpecialArmorModelRenderer.offsetMap.getOrDefault(anchorType.name + "_y", -32768F) != offsetY || SpecialArmorModelRenderer.offsetMap.getOrDefault(anchorType.name + "_z", -32768F) != offsetZ) {
				SpecialArmorModelRenderer.offsetMap.put(anchorType.name + "_x", offsetX);
				SpecialArmorModelRenderer.offsetMap.put(anchorType.name + "_y", offsetY);
				SpecialArmorModelRenderer.offsetMap.put(anchorType.name + "_z", offsetZ);

				shouldUpdate = true;
			}

		}

		if (shouldUpdate)
			this.model = new DarkArmorModel(EquipmentSlotType.MAINHAND);

	     return (DarkArmorModel) this.model;
	 }
	 */
}