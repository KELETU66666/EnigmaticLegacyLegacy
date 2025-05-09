package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.tabEnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemInfernalShield extends ItemShield {

    public ItemInfernalShield() {
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "infernal_shield"));
        this.setTranslationKey("infernal_shield");
        this.setMaxDamage(10000);
        this.setCreativeTab(tabEnigmaticLegacy);
        this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }
    @Override
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
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield1"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield2"));
            tooltip.add("");
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield3"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield4"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield5"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield6"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield7"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield8"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield9"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield10"));
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.infernalShield11"));
        } else {
            tooltip.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        tooltip.add("");
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (SuperpositionHandler.hasCursed(player) || SuperpositionHandler.hasBlessed(player)) {
            player.setActiveHand(hand);
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        } else
            return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity holder, int itemSlot, boolean isSelected) {
        if (holder instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) holder;
            if (player.isBurning() && SuperpositionHandler.hasCursed(player) || SuperpositionHandler.hasBlessed(player)) {
                if (player.getHeldItemMainhand() == stack || player.getHeldItemOffhand() == stack) {
                    player.extinguish();
                }
            }
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment instanceof EnchantmentDurability || super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 16;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.format("item.infernal_shield.name");
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.EPIC;
    }
}