package keletu.enigmaticlegacy.mixins.early;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ContainerEnchantment.class)
public abstract class MixinContainerEnchantment extends Container {

    @Final
    @Shadow
    private World world;

    @Final
    @Shadow
    private BlockPos position;

    @Shadow
    protected abstract List<EnchantmentData> getEnchantmentList(ItemStack stack, int enchantSlot, int level);

    @Inject(at = @At(value = "HEAD"/*, target = "Lnet/minecraft/entity/player/EntityPlayer;onEnchant(Lnet/minecraft/item/ItemStack;I)V"*/), method = "enchantItem", cancellable = true)
    private void onEnchantedItem(EntityPlayer player, int clickedID, CallbackInfoReturnable<Boolean> info) {
        // Evaluating expression promts error assuming incompatible types,
        // so we need to forget our own class to avoid alerting the compiler
        ContainerEnchantment container = (ContainerEnchantment) (Object) this;

        if (SuperpositionHandler.hasCursed(player))
            if (SuperpositionHandler.hasPearl(player) || BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.enchanterPearl) != -1) {
                ItemStack itemstack = container.tableInventory.getStackInSlot(0);
                int levelsRequired = clickedID + 1;

                if (container.enchantLevels[clickedID] > 0 && !itemstack.isEmpty() && (player.experienceLevel >= clickedID && player.experienceLevel >= container.enchantLevels[clickedID] || player.capabilities.isCreativeMode)) {
                    ItemStack enchantedItem = itemstack;

                    if (!this.world.isRemote) {
                        List<EnchantmentData> list = getEnchantmentList(itemstack, clickedID, container.enchantLevels[clickedID]);

                        if (!list.isEmpty()) {
                            ItemStack doubleRoll = EnchantmentHelper.addRandomEnchantment(player.getRNG(), enchantedItem.copy(), Math.min(container.enchantLevels[clickedID] + 7, 40), true);

                            player.onEnchant(itemstack, levelsRequired);
                            boolean flag = itemstack.getItem() == Items.BOOK;

                            if (flag) {
                                enchantedItem = new ItemStack(Items.ENCHANTED_BOOK);
                                container.tableInventory.setInventorySlotContents(0, enchantedItem);
                            }

                            for (int j = 0; j < list.size(); ++j) {
                                EnchantmentData enchantmentdata = list.get(j);

                                if (flag) {
                                    ItemEnchantedBook.addEnchantment(enchantedItem, enchantmentdata);
                                } else {
                                    enchantedItem.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
                                }
                            }

                            enchantedItem = SuperpositionHandler.mergeEnchantments(enchantedItem, doubleRoll, false, false);
                            container.tableInventory.setInventorySlotContents(0, enchantedItem);

                            player.addStat(StatList.ITEM_ENCHANTED);

                            if (player instanceof EntityPlayerMP) {
                                CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) player, itemstack, levelsRequired);
                            }

                            container.tableInventory.markDirty();
                            container.xpSeed = player.getXPSeed();
                            this.onCraftMatrixChanged(container.tableInventory);
                            this.world.playSound(null, this.position, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);
                        }
                    }

                    info.setReturnValue(true);
                } else {
                    info.setReturnValue(false);
                }
            }
    }

    @SideOnly(Side.CLIENT)
    @Inject(at = @At("HEAD"), method = "getLapisAmount", cancellable = true)
    public void onGetLapisAmount(CallbackInfoReturnable<Integer> info) {

        Object forgottenObject = this;
        ContainerEnchantment container = (ContainerEnchantment) forgottenObject;
        EntityPlayer containerUser = null;

        for (Slot slot : container.inventorySlots) {
            if (slot.inventory instanceof InventoryPlayer) {
                InventoryPlayer playerInv = (InventoryPlayer) slot.inventory;
                containerUser = playerInv.player;
                break;
            }
        }

        if (containerUser != null) {
            if (SuperpositionHandler.hasCursed(containerUser))
                if (SuperpositionHandler.hasPearl(containerUser) || BaublesApi.isBaubleEquipped(containerUser, EnigmaticLegacy.enchanterPearl) != -1) {
                    info.setReturnValue(64);
                }
        }
    }

}