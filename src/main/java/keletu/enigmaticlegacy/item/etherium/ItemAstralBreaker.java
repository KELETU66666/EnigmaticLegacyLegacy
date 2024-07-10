package keletu.enigmaticlegacy.item.etherium;

import com.google.common.collect.Sets;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
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
import java.util.Set;

public class ItemAstralBreaker extends ItemTool implements IEtheriumTool {

    private static final Set<Block> EFFECTIVE_ON_ALL = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.CONCRETE_POWDER, Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.DOUBLE_STONE_SLAB, Blocks.GOLDEN_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.STONE_SLAB, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);

    public ItemAstralBreaker() {
        super(4F, -2.8F, EnigmaticLegacy.ETHERIUM, EFFECTIVE_ON_ALL);
        setTranslationKey("astral_breaker");
        setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, "astral_breaker"));

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
                    if (f >= 0F && !(state1.getMaterial() instanceof MaterialLiquid)) {
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
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.astralBreaker1"));
            list.add("");

            list.add(I18n.format("tooltip.enigmaticlegacy.astralBreaker2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.astralBreaker3"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.astralBreaker4"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        if (!this.areaEffectsAllowed(stack)) {
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.aoeDisabled"));
        }
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(IBlockState blockIn)
    {
        Block block = blockIn.getBlock();

        if (block == Blocks.OBSIDIAN)
        {
            return this.toolMaterial.getHarvestLevel() == 3;
        }
        else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE)
        {
            if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK)
            {
                if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE)
                {
                    if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE)
                    {
                        if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE)
                        {
                            if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE)
                            {
                                Material material = blockIn.getMaterial();

                                if (material == Material.ROCK)
                                {
                                    return true;
                                }
                                else if (material == Material.IRON)
                                {
                                    return true;
                                }
                                else
                                {
                                    return material == Material.ANVIL;
                                }
                            }
                            else
                            {
                                return this.toolMaterial.getHarvestLevel() >= 2;
                            }
                        }
                        else
                        {
                            return this.toolMaterial.getHarvestLevel() >= 1;
                        }
                    }
                    else
                    {
                        return this.toolMaterial.getHarvestLevel() >= 1;
                    }
                }
                else
                {
                    return this.toolMaterial.getHarvestLevel() >= 2;
                }
            }
            else
            {
                return this.toolMaterial.getHarvestLevel() >= 2;
            }
        }
        else
        {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
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