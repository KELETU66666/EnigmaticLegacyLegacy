package keletu.enigmaticlegacy.proxy;

import keletu.enigmaticlegacy.client.LayerAmulet;
import keletu.enigmaticlegacy.client.LayerCharm;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{
    private static final Random random = new Random();

    @Override
    public void spawnBonemealParticles(World worldIn, BlockPos pos, int amount)
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
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float)pos.getX() + random.nextFloat(), (double)pos.getY() + (double)random.nextFloat() * iblockstate.getBoundingBox(worldIn, pos).maxY, (float)pos.getZ() + random.nextFloat(), d0, d1, d2);
            }
        }
        else
        {
            for (int i1 = 0; i1 < amount; ++i1)
            {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float)pos.getX() + random.nextFloat(), (double)pos.getY() + (double)random.nextFloat() * 1.0f, (float)pos.getZ() + random.nextFloat(), d0, d1, d2);
            }
        }
    }

    public static void addRenderLayers() {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

        addLayersToSkin(skinMap.get("default"));
        addLayersToSkin(skinMap.get("slim"));
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer) {
        //renderPlayer.addLayer(new LayerDrinkingHat(renderPlayer));
        renderPlayer.addLayer(new LayerAmulet(renderPlayer));
        renderPlayer.addLayer(new LayerCharm(renderPlayer));
        //renderPlayer.addLayer(new LayerBelt(renderPlayer));
        //renderPlayer.addLayer(new LayerCloak(renderPlayer));
        //renderPlayer.addLayer(new LayerNightVisionGoggles(renderPlayer));
        //renderPlayer.addLayer(new LayerSnorkel(renderPlayer));
    }
}