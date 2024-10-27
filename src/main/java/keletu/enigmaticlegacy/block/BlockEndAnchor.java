package keletu.enigmaticlegacy.block;

import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Random;

public class BlockEndAnchor extends EnigmaticBaseBlock {
    public static final PropertyInteger CHARGE = PropertyInteger.create("charges", 0, 4);

    public BlockEndAnchor(String name, Material material, String usedTool, int toolStrength, SoundType soundType) {
        super(name, material, usedTool, toolStrength, soundType);

        this.setDefaultState(this.blockState.getBaseState().withProperty(CHARGE, 0));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(CHARGE, MathHelper.clamp(meta, 0, 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CHARGE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHARGE);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(CHARGE) != 0) {
            for (int i = 0; i < 3; ++i) {
                int j = rand.nextInt(2) * 2 - 1;
                int k = rand.nextInt(2) * 2 - 1;
                double d0 = pos.getX() + 0.5D + 0.25D * j;
                double d1 = pos.getY() + rand.nextFloat();
                double d2 = pos.getZ() + 0.5D + 0.25D * k;
                double d3 = rand.nextFloat() * j;
                double d4 = (rand.nextFloat() - 0.5D) * 0.125D;
                double d5 = rand.nextFloat() * k;
                worldIn.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        Field fld = ReflectionHelper.findField(EntityPlayer.class, "spawnPos");
        fld.setAccessible(true);
        try {
            BlockPos spawnPos = (BlockPos) fld.get(playerIn);

            if (playerIn.isSneaking() && state.getValue(CHARGE) != 0) {
                if (!playerIn.isCreative())
                    playerIn.inventory.addItemStackToInventory(new ItemStack(Items.ENDER_PEARL));
                worldIn.setBlockState(pos, state.withProperty(CHARGE, state.getValue(CHARGE) - 1), 3);
                worldIn.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            } else if (state.getValue(CHARGE) < 4 && stack.getItem() == Items.ENDER_PEARL) {
                worldIn.setBlockState(pos, state.cycleProperty(CHARGE));
                worldIn.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!playerIn.isCreative())
                    stack.shrink(1);
                return true;
            } else if (state.getValue(CHARGE) != 0 && (spawnPos != pos.up() || playerIn.getSpawnDimension() != worldIn.provider.getDimension())) {
                SuperpositionHandler.setPersistentBoolean(playerIn, "useAnchor", true);
                playerIn.setSpawnDimension(worldIn.provider.getDimension());
                playerIn.setSpawnPoint(pos.up(), true);
                worldIn.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return true;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}