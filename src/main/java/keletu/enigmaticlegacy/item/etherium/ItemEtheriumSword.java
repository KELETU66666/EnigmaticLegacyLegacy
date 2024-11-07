package keletu.enigmaticlegacy.item.etherium;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.util.Vector3;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEtheriumSword extends ItemSword implements IEtheriumTool {

    public ItemEtheriumSword() {
        super(EnigmaticLegacy.ETHERIUM);
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "etherium_sword"));
        this.setTranslationKey("etherium_sword");

        this.setCreativeTab(EnigmaticLegacy.tabEnigmaticLegacy);
    }
    
    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
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
            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumSword1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumSword2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumSword3"));
            list.add("");
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.etheriumSword4") + 3 + I18n.format("tooltip.enigmaticlegacy.etheriumSword4_1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumSword5"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        if (!this.areaEffectsAllowed(stack)) {
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.abilityDisabled"));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (hand == EnumHand.OFF_HAND)
            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));

        if (player.isSneaking()) {
            this.toggleAreaEffects(player, stack);

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            if (!player.getCooldownTracker().hasCooldown(this) && this.areaEffectsEnabled(player, stack)) {
                Vector3 look = new Vector3(player.getLookVec());
                Vector3 dir = look.multiply(1D);

                if (!(dir.x == 0 && dir.z == 0))
                    player.knockBack(player, 1.0F, dir.x, dir.z);

                if (!player.world.isRemote)
                    world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, (float) (0.6F + (Math.random() * 0.1D)));

                player.getCooldownTracker().setCooldown(this, 60);

                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
}