package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import static keletu.enigmaticlegacy.ELConfigs.invertShift;
import static keletu.enigmaticlegacy.ELConfigs.range;
import keletu.enigmaticlegacy.core.Vector3;
import keletu.enigmaticlegacy.event.ELEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMagnetRing extends ItemBaseBauble {

    public ItemMagnetRing() {
        super("magnet_ring", EnumRarity.RARE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.magnetRing1") + range + I18n.format("tooltip.enigmaticlegacy.magnetRing1_1"));
            list.add(invertShift ? I18n.format("tooltip.enigmaticlegacy.magnetRing2_alt") : I18n.format("tooltip.enigmaticlegacy.magnetRing2"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.RING;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        if ((invertShift != living.isSneaking()) || !(living instanceof EntityPlayer))
            return;

        double x = living.posX;
        double y = living.posY + 0.75;
        double z = living.posZ;

        List<EntityItem> items = living.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range));
        int pulled = 0;
        for (EntityItem item : items)
            if (this.canPullItem(item)) {
                if (pulled > 200) {
                    break;
                }

                //if (!ELEvents.canPickStack((EntityPlayer) living, item.getItem())) {
                //	continue;
                //}

                ELEvents.setEntityMotionFromVector(item, new Vector3(x, y, z), 0.45F);

                //for (int counter = 0; counter <= 2; counter++)
                //	living.world.addParticle(ParticleTypes.WITCH, item.getPosX(), item.getPosY() - item.getYOffset() + item.getHeight() / 2, item.getPosZ(), (Math.random() - 0.5D) * 0.1D, (Math.random() - 0.5D) * 0.1D, (Math.random() - 0.5D) * 0.1D);
                pulled++;
            }

    }

    protected boolean canPullItem(EntityItem item) {
        ItemStack stack = item.getItem();
        return !item.isDead && !stack.isEmpty() && !item.getEntityData().getBoolean("PreventRemoteMovement");
    }
}