package keletu.enigmaticlegacy.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderSlayer extends ItemSword {

    public ItemEnderSlayer() {
        super(EnigmaticLegacy.ENDER_SLAYER);
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "ender_slayer"));
        this.setTranslationKey("ender_slayer");

        this.setCreativeTab(EnigmaticLegacy.tabEnigmaticLegacy);
    }

    public boolean isEndDweller(EntityLivingBase entity) {
        return entity instanceof EntityEnderman || entity instanceof EntityDragon || entity instanceof EntityShulker || entity instanceof EntityEndermite || EnigmaticConfigs.enderSlayerEndDwellers.contains(EntityList.getKey(entity));
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", EnigmaticConfigs.enderSlayerAttackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", EnigmaticConfigs.enderSlayerAttackSpeed, 0));
        }

        return multimap;
    }


    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }


    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return super.getIsRepairable(toRepair, repair) || repair.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN);
    }


    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemIndestructible item = new EntityItemIndestructible(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            item.setThrower(((EntityItem) location).getThrower());
            item.setOwner(((EntityItem) location).getOwner());
        }

        return item;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer3", Math.round(EnigmaticConfigs.enderSlayerEndDamageBonus * 100) + "%"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer4", Math.round(EnigmaticConfigs.enderSlayerEndKnockbackBonus * 100) + "%"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer6"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer7"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer8"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer9"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer10"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer11"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer12"));
            list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer13"));
        } else {
            String stackName = stack.getDisplayName();

            if (stackName.substring(1, stackName.length() - 1).equals("§dThe Ender Slapper")) {
                list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer1_alt"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer2_alt"));
            } else {
                list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer1"));
                list.add(I18n.format("tooltip.enigmaticlegacy.enderSlayer2"));
            }
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        list.add("");
    }

}