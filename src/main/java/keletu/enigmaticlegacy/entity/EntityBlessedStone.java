package keletu.enigmaticlegacy.entity;

import keletu.enigmaticlegacy.util.helper.ItemNBTHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBlessedStone extends EntityItemIndestructible {

    public EntityBlessedStone(World world) {
        super(world);
    }

    public EntityBlessedStone(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityBlessedStone(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float value) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            ItemStack stack = this.getItem();
            if (!stack.isEmpty() && source == DamageSource.LIGHTNING_BOLT) {
                if (!ItemNBTHelper.getBoolean(stack, "Hardcore", false)) {
                    stack.addEnchantment(Enchantments.UNBREAKING, 10);
                    ItemNBTHelper.setBoolean(stack, "Hardcore", true);
                }
                return false;
            }
        }
        return false;
    }

    public void onUpdate()
    {
        super.onUpdate();
        if (!world.isRemote && ItemNBTHelper.getBoolean(this.getItem(), "Hardcore", false)) {
            WorldServer server = (WorldServer) world;
            if (this.rand.nextInt(3) == 0)
                server.spawnParticle(EnumParticleTypes.END_ROD, this.posX, this.posY + 0.5, this.posZ, 1, 0.2, 0.2, 0.2, 0.02D);
        }
    }
}