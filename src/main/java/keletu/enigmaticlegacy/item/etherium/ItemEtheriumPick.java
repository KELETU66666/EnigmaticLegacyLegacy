package keletu.enigmaticlegacy.item.etherium;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEtheriumPick extends ItemPickaxe implements IEtheriumTool {
    public ItemEtheriumPick() {
        super(EnigmaticLegacy.ETHERIUM);
        setTranslationKey("etherium_pickaxe");
        setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "etherium_pickaxe"));

        setCreativeTab(CreativeTabs.TOOLS);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        boolean ret = super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
        if (this.areaEffectsAllowed(stack)) {
            if (!(entityLiving instanceof EntityPlayer) || worldIn.isRemote) {
                return ret;
            }

            EntityPlayer player = (EntityPlayer) entityLiving;
            EnumFacing facing = entityLiving.getHorizontalFacing();

            if (entityLiving.rotationPitch < -45.0F) {
                facing = EnumFacing.UP;
            } else if (entityLiving.rotationPitch > 45.0F) {
                facing = EnumFacing.DOWN;
            }

            boolean yAxis = facing.getAxis() == EnumFacing.Axis.Y;
            boolean xAxis = facing.getAxis() == EnumFacing.Axis.X;

            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1 && !stack.isEmpty(); ++j) {
                    if (i == 0 && j == 0) {
                        continue;
                    }

                    BlockPos pos1;
                    if (yAxis) {
                        pos1 = pos.add(i, 0, j);
                    } else if (xAxis) {
                        pos1 = pos.add(0, i, j);
                    } else {
                        pos1 = pos.add(i, j, 0);
                    }

                    //:Replicate logic of PlayerInteractionManager.tryHarvestBlock(pos1)
                    IBlockState state1 = worldIn.getBlockState(pos1);
                    float f = state1.getBlockHardness(worldIn, pos1);
                    if (f >= 0F && (state1.getMaterial().equals(Material.IRON) || state1.getMaterial().equals(Material.ROCK) || state1.getMaterial().equals(Material.GLASS) || state1.getMaterial().equals(Material.PACKED_ICE) || state1.getMaterial().equals(Material.ICE) || state1.getMaterial().equals(Material.DRAGON_EGG))) {
                        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(worldIn, pos1, state1, player);
                        MinecraftForge.EVENT_BUS.post(event);
                        if (!event.isCanceled()) {
                            Block block = state1.getBlock();
                            if ((block instanceof BlockCommandBlock || block instanceof BlockStructure) && !player.canUseCommandBlock()) {
                                worldIn.notifyBlockUpdate(pos1, state1, state1, 3);
                                continue;
                            }
                            TileEntity tileentity = worldIn.getTileEntity(pos1);
                            if (tileentity != null) {
                                Packet<?> pkt = tileentity.getUpdatePacket();
                                if (pkt != null) {
                                    ((EntityPlayerMP) player).connection.sendPacket(pkt);
                                }
                            }

                            boolean canHarvest = block.canHarvestBlock(worldIn, pos1, player);
                            boolean destroyed = block.removedByPlayer(state1, worldIn, pos1, player, canHarvest);
                            if (destroyed) {
                                block.breakBlock(worldIn, pos1, state1);
                            }
                            if (canHarvest && destroyed) {
                                block.harvestBlock(worldIn, player, pos1, state1, tileentity, stack);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (player.isSneaking()) {
            this.toggleAreaEffects(player, stack);

            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else
            return super.onItemRightClick(world, player, hand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.etheriumPickaxe1"));
            list.add("");

            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumPickaxe2"));

            list.add(I18n.format("tooltip.enigmaticlegacy.etheriumPickaxe3"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        if (!this.areaEffectsAllowed(stack)) {
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.aoeDisabled"));
        }
    }
}