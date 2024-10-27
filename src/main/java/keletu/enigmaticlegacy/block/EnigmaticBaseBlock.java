package keletu.enigmaticlegacy.block;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class EnigmaticBaseBlock extends Block {
    public EnigmaticBaseBlock(String name, Material blockMaterialIn, String usedTool, int toolStrength, SoundType soundType) {
        super(blockMaterialIn, blockMaterialIn.getMaterialMapColor());
        this.setRegistryName(new ResourceLocation(EnigmaticLegacy.MODID, name));
        this.setTranslationKey(name);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setHarvestLevel(usedTool, toolStrength);
        this.setSoundType(soundType);
        this.setCreativeTab(EnigmaticLegacy.tabEnigmaticLegacy);
    }

    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon)
    {
        return worldObj.getBlockState(pos).getMaterial() == Material.IRON;
    }
}
