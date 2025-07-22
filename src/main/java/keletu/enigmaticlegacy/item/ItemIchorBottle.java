package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import static keletu.enigmaticlegacy.EnigmaticLegacy.tabEnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemIchorBottle extends Item {


    public ItemIchorBottle() {
        this.setMaxStackSize(1);
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "ichor_bottle"));
        this.setTranslationKey("ichor_bottle");

        this.setCreativeTab(tabEnigmaticLegacy);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.ichorBottle1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.ichorBottle2"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean hasEffect(ItemStack pStack) {
        return true;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }


    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    @Override
    public Item setContainerItem(Item containerItem) {
        return Items.GLASS_BOTTLE;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase player) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            BaublesContainer container = BaublesApi.getBaublesContainer(playerMP);

            if (!SuperpositionHandler.hasPersistentTag(playerMP, "ConsumedIchorBottle")) {
                SuperpositionHandler.setPersistentBoolean(playerMP, "ConsumedIchorBottle", true);
                container.grow(BaubleType.AMULET, 1);
                playerMP.world.playSound(null, playerMP.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1F);
            }

            double multiplier = 1;
            stack.shrink(1);
            playerMP.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));

            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) (1200 * multiplier), 2, false, true));
            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, (int) (800 * multiplier), 4, false, true));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, (int) (1600 * multiplier), 2, false, true));
            player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, (int) (3000 * multiplier), 0, false, true));
        }
        return super.onItemUseFinish(stack, worldIn, player);
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

}