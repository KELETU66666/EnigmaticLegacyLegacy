package keletu.enigmaticlegacy.proxy;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.client.LayerAmulet;
import keletu.enigmaticlegacy.client.LayerCharm;
import keletu.enigmaticlegacy.client.LayerHeadwear;
import keletu.enigmaticlegacy.entity.EntityHarmlessLightningBolt;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private static final Random random = new Random();

    @Override
    public void spawnBonemealParticles(World worldIn, BlockPos pos, int amount) {
        if (amount == 0) {
            amount = 15;
        }

        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getMaterial() != Material.AIR) {
            for (int i = 0; i < amount; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float) pos.getX() + random.nextFloat(), (double) pos.getY() + (double) random.nextFloat() * iblockstate.getBoundingBox(worldIn, pos).maxY, (float) pos.getZ() + random.nextFloat(), d0, d1, d2);
            }
        } else {
            for (int i1 = 0; i1 < amount; ++i1) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (float) pos.getX() + random.nextFloat(), (double) pos.getY() + (double) random.nextFloat() * 1.0f, (float) pos.getZ() + random.nextFloat(), d0, d1, d2);
            }
        }
    }

    public void renderEntities(){
        RenderingRegistry.registerEntityRenderingHandler(EntityHarmlessLightningBolt.class, new RenderLightningBolt(Minecraft.getMinecraft().getRenderManager()));
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
        renderPlayer.addLayer(new LayerHeadwear(renderPlayer));
        //renderPlayer.addLayer(new LayerBelt(renderPlayer));
        //renderPlayer.addLayer(new LayerCloak(renderPlayer));
        //renderPlayer.addLayer(new LayerNightVisionGoggles(renderPlayer));
        //renderPlayer.addLayer(new LayerSnorkel(renderPlayer));
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void displayReviveAnimation(int entityID, int reviveType) {
        EntityPlayer player = this.getClientPlayer();
        Entity entity = player.world.getEntityByID(entityID);

        if (entity != null) {
            Item item = reviveType == 0 ? /*EnigmaticItems.COSMIC_SCROLL*/Items.STICK : EnigmaticLegacy.the_cube;
            int i = 40;
            Minecraft.getMinecraft().effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.TOTEM, 30);

            int amount = 50;

            for (int counter = 0; counter <= amount; counter++) {
                player.world.spawnParticle(EnumParticleTypes.FLAME, true, entity.posX + (Math.random() - 0.5), entity.posY + (entity.getEyeHeight() / 2) + (Math.random() - 0.5), entity.posZ + (Math.random() - 0.5), (Math.random() - 0.5D) * 0.2, (Math.random() - 0.5D) * 0.2, (Math.random() - 0.5D) * 0.2);
            }

            player.world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);

            if (entity == player) {
                ItemStack stack = SuperpositionHandler.getAdvancedBaubles(player);

                if (stack == null) {
                    stack = new ItemStack(item, 1);
                }

                Minecraft.getMinecraft().entityRenderer.displayItemActivation(stack);
            }
        }
    }
}