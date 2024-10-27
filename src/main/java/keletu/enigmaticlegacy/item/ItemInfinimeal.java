package keletu.enigmaticlegacy.item;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemInfinimeal extends ItemBase {

    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    public ItemInfinimeal() {
        super("infinimeal", EnumRarity.UNCOMMON);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.infinimeal1"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.infinimeal2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.infinimeal3"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    public static boolean applyBonemeal(ItemStack stack, World world, BlockPos target, EntityPlayer player, @javax.annotation.Nullable EnumHand hand) {
        IBlockState iblockstate = world.getBlockState(target);
        Block block = iblockstate.getBlock();

        int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, world, target, iblockstate, stack, hand);
        if (hook != 0) return hook > 0;

        if (block instanceof IGrowable) {
            IGrowable igrowable = (IGrowable) block;

            if (igrowable.canGrow(world, target, iblockstate, world.isRemote)) {
                if (!world.isRemote) {
                    if (igrowable.canUseBonemeal(world, world.rand, target, iblockstate)) {
                        igrowable.grow(world, world.rand, target, iblockstate);
                    }
                }

                return true;
            }
        } else if (block instanceof BlockCactus || block instanceof BlockReed) {
            BlockPos topMostPos = findTopmostGrowable(world, target, block, true);
            IBlockState topMostState = world.getBlockState(topMostPos);

            if (topMostState.getPropertyKeys().contains(AGE) && world.isAirBlock(topMostPos.up())) {
                int age = topMostState.getValue(AGE);

                int plantHeight;
                for (plantHeight = 1; world.getBlockState(topMostPos.down(plantHeight)).getBlock().equals(block); ++plantHeight) {
                }

                if (plantHeight >= 3)
                    return false;

                age += world.rand.nextInt(20);
                world.setBlockState(topMostPos, topMostState.withProperty(AGE, Math.min(age, 15)), 4);

                if (world instanceof WorldServer) {
                    block.randomTick(world, topMostPos, world.getBlockState(topMostPos), world.rand);
                }

                return true;
            }
        }/*else if (block instanceof BlockVine) {
            if (!block.getTickRandomly())
                return false;

            if (world.isRemote) {
                EnigmaticLegacy.proxy.spawnBonemealParticles(world, target, 0);
            }

            int cycles = 7 + world.rand.nextInt(7);

            if (world instanceof WorldServer) {
                for (int i = 0; i <= cycles; i++) {
                    block.randomTick((WorldServer) world, target, iblockstate, world.rand);
                }

                world.notifyNeighborsOfStateChange(target.down(), Blocks.AIR, false);
            }
        }*/else if (block instanceof BlockNetherWart){
            if (!block.getTickRandomly())
                return false;

            int cycles = 1 + world.rand.nextInt(1);
            cycles *= 11;

            if (world instanceof WorldServer) {
                for (int i = 0; i <= cycles; i++) {
                    block.randomTick(world, target, iblockstate, world.rand);
                }
            }

            return true;
        }

        return false;
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
            return EnumActionResult.FAIL;
        } else {
            if (applyBonemeal(itemstack, worldIn, pos, player, hand)) {
                if (!worldIn.isRemote) {
                    worldIn.playEvent(2005, pos, 0);
                }

                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    private static BlockPos findTopmostGrowable(World world, BlockPos pos, Block block, boolean goUp) {
        BlockPos top = pos;

        while (true) {
            if (world.getBlockState(top) != null && world.getBlockState(top).getBlock() == block) {
                BlockPos nextUp = goUp ? top.up() : top.down();

                if (world.getBlockState(nextUp) == null || world.getBlockState(nextUp).getBlock() != block)
                    return top;
                else {
                    top = nextUp;
                    continue;
                }
            } else
                return pos;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount)
    {
        if (amount == 0)
        {
            amount = 15;
        }

        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getMaterial() != Material.AIR)
        {
            for (int i = 0; i < amount; ++i)
            {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float)pos.getX() + itemRand.nextFloat(), (double)pos.getY() + (double)itemRand.nextFloat() * iblockstate.getBoundingBox(worldIn, pos).maxY, (float)pos.getZ() + itemRand.nextFloat(), d0, d1, d2);
            }
        }
        else
        {
            for (int i1 = 0; i1 < amount; ++i1)
            {
                double d0 = itemRand.nextGaussian() * 0.02D;
                double d1 = itemRand.nextGaussian() * 0.02D;
                double d2 = itemRand.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float)pos.getX() + itemRand.nextFloat(), (double)pos.getY() + (double)itemRand.nextFloat() * 1.0f, (float)pos.getZ() + itemRand.nextFloat(), d0, d1, d2);
            }
        }
    }
}