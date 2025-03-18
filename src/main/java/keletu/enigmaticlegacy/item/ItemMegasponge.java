package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.packet.PacketPortalParticles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMegasponge extends ItemBaseBauble {

    public ItemMegasponge() {
        super("mega_sponge", EnumRarity.UNCOMMON);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.megaSponge1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.megaSponge2"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        if (living instanceof EntityPlayer & !living.world.isRemote) {
            EntityPlayer player = (EntityPlayer) living;

            if (!player.getCooldownTracker().hasCooldown(this) && player.isInWater()) {
                this.tryAbsorb(player.world, player.getPosition());
                player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
                EnigmaticLegacy.packetInstance.sendToAllAround(new PacketPortalParticles(player.posX, player.posY + (player.getEyeHeight() / 2), player.posZ, 40, 1.0D, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64));
                player.getCooldownTracker().setCooldown(this, 20);

            }

        }
    }

    private boolean soakUp(World world, BlockPos pos) {
        for (int x = -EnigmaticConfigs.radius; x <= EnigmaticConfigs.radius; x++) {
            for (int y = -EnigmaticConfigs.radius; y <= EnigmaticConfigs.radius; y++) {
                for (int z = -EnigmaticConfigs.radius; z <= EnigmaticConfigs.radius; z++) {
                    final BlockPos targetPos = pos.add(x, y, z);

                    Material material = world.getBlockState(targetPos).getMaterial();
                    if (material.isLiquid() && material != Material.LAVA) {
                        world.setBlockState(targetPos, Blocks.AIR.getDefaultState(), 2);
                        world.notifyNeighborsOfStateChange(targetPos, Blocks.AIR, false);
                    }

                }
            }
        }

        return false;
    }

    protected void tryAbsorb(World worldIn, BlockPos pos) {
        if (!this.soakUp(worldIn, pos)) {
            worldIn.playEvent(2001, pos, Block.getIdFromBlock(Blocks.WATER));
        }
    }
}