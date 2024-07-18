package keletu.enigmaticlegacy.item;

import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.api.ExtendedBaubleType;
import keletu.enigmaticlegacy.api.IExtendedBauble;
import keletu.enigmaticlegacy.core.ExperienceHelper;
import keletu.enigmaticlegacy.core.ItemNBTHelper;
import keletu.enigmaticlegacy.event.ELEvents;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemExperienceScroll extends ItemBase implements IExtendedBauble {


    public final String TAG_ABSORPTION = "AbsorptionMode";
    public final int xpPortion = 5;

    public ItemExperienceScroll() {
        super("xp_scroll", EnumRarity.EPIC);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, World par2EntityPlayer, List<String> par3List, ITooltipFlag par4) {

        String cMode;
        if (!ItemNBTHelper.getBoolean(par1ItemStack, "IsActive", false))
            cMode = I18n.format("tooltip.enigmaticlegacy.xpTomeDeactivated");
        else if (ItemNBTHelper.getBoolean(par1ItemStack, this.TAG_ABSORPTION, true))
            cMode = I18n.format("tooltip.enigmaticlegacy.xpTomeAbsorption");
        else
            cMode = I18n.format("tooltip.enigmaticlegacy.xpTomeExtraction");

        if(GuiScreen.isShiftKeyDown()){
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome1"));
            par3List.add("");
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome2"));
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome3"));
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome4"));
            par3List.add("");
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome5"));
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome6"));
            par3List.add("");
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome7"));
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome8"));
            par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTome9"));

        }
        else {
            par3List.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));

        }
        par3List.add("");
        par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTomeMode") + " " + cMode);
        par3List.add("");
        par3List.add(I18n.format("tooltip.enigmaticlegacy.xpTomeExp"));
        par3List.add(ItemNBTHelper.getInt(par1ItemStack, "XPStored", 0) + " " + I18n.format("tooltip.enigmaticlegacy.xpTomeUnits") + " " + ExperienceHelper.getLevelForExperience(ItemNBTHelper.getInt(par1ItemStack, "XPStored", 0)) + " " + I18n.format("tooltip.enigmaticlegacy.xpTomeLevels"));

    }
    
    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean b) {

        if (!(entity instanceof EntityPlayer) || world.isRemote || !ItemNBTHelper.getBoolean(itemstack, "IsActive", false))
            return;

        boolean action = false;

        EntityPlayer player = (EntityPlayer) entity;

        if (ItemNBTHelper.getBoolean(itemstack, this.TAG_ABSORPTION, true)) {

            if (ExperienceHelper.getPlayerXP(player) >= this.xpPortion) {
                ExperienceHelper.drainPlayerXP(player, this.xpPortion);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + this.xpPortion);
                action = true;
            }
            else if (ExperienceHelper.getPlayerXP(player) > 0 & ExperienceHelper.getPlayerXP(player) < this.xpPortion) {
                int exp = ExperienceHelper.getPlayerXP(player);
                ExperienceHelper.drainPlayerXP(player, exp);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + exp);
                action = true;
            }


        } else {

            int xp = ItemNBTHelper.getInt(itemstack, "XPStored", 0);

            if (xp >= this.xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", xp-this.xpPortion);
                ExperienceHelper.addPlayerXP(player, this.xpPortion);
                action = true;
            } else if (xp > 0 & xp < this.xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", 0);
                ExperienceHelper.addPlayerXP(player, xp);
                action = true;
            }

        }

        if (action)
            player.inventoryContainer.detectAndSendChanges();

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!player.isSneaking()) {

            if (ItemNBTHelper.getBoolean(stack, this.TAG_ABSORPTION, true)) {
                ItemNBTHelper.setBoolean(stack, this.TAG_ABSORPTION, false);
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, (float) (0.4F + (Math.random() * 0.1F)));
            }
            else {
                ItemNBTHelper.setBoolean(stack, this.TAG_ABSORPTION, true);
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, (float) (0.4F + (Math.random() * 0.1F)));
            }
        } else {

            if (ItemNBTHelper.getBoolean(stack, "IsActive", false)) {
                ItemNBTHelper.setBoolean(stack, "IsActive", false);
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2F)));
            }
            else {
                ItemNBTHelper.setBoolean(stack, "IsActive", true);
                world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2F)));
            }
        }



        player.swingArm(hand);



        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean isFull3D() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {

        return ItemNBTHelper.getBoolean(stack, "IsActive", false);

    }


    @Override
    public EnumRarity getRarity(ItemStack itemStack)
    {
        return EnumRarity.EPIC;
    }

    @Override
    public ExtendedBaubleType getBaubleType(ItemStack itemStack) {
        return ExtendedBaubleType.SCROLL;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase livingPlayer) {

        if (!(livingPlayer instanceof EntityPlayer) || livingPlayer.world.isRemote || !ItemNBTHelper.getBoolean(itemstack, "IsActive", false))
            return;

        EntityPlayer player = (EntityPlayer) livingPlayer;
        World world = player.world;

        if (ItemNBTHelper.getBoolean(itemstack, this.TAG_ABSORPTION, true)) {

            if (ExperienceHelper.getPlayerXP(player) >= this.xpPortion) {
                ExperienceHelper.drainPlayerXP(player, this.xpPortion);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + this.xpPortion);
            }
            else if (ExperienceHelper.getPlayerXP(player) > 0 & ExperienceHelper.getPlayerXP(player) < this.xpPortion) {
                int exp = ExperienceHelper.getPlayerXP(player);
                ExperienceHelper.drainPlayerXP(player, exp);
                ItemNBTHelper.setInt(itemstack, "XPStored", ItemNBTHelper.getInt(itemstack, "XPStored", 0) + exp);
            }


        } else {

            int xp = ItemNBTHelper.getInt(itemstack, "XPStored", 0);

            if (xp >= this.xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", xp-this.xpPortion);
                ExperienceHelper.addPlayerXP(player, this.xpPortion);
            } else if (xp > 0 & xp < this.xpPortion) {
                ItemNBTHelper.setInt(itemstack, "XPStored", 0);
                ExperienceHelper.addPlayerXP(player, xp);
            }

        }

        List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, SuperpositionHandler.getBoundingBoxAroundEntity(player, ELConfigs.xpCollectionRange));
        for (EntityXPOrb processed : orbs) {
            if (processed.isDead) {
                continue;
            }

            player.xpCooldown = 0;
            processed.onCollideWithPlayer(player);
            //processed.setPositionAndUpdate(player.posX, player.posY, player.posZ);
        }
    }
}
