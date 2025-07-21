package keletu.enigmaticlegacy.entity;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityHarmlessLightningBolt extends EntityLightningBolt {
    /**
     * Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.
     */
    private int lightningState;
    /**
     * Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.
     */
    private int boltLivingTime;

    public float amount;

    public EntityHarmlessLightningBolt(World worldIn) {
        this(worldIn, 0.0f, 0.0f, 0.0f, 5.0f);
    }

    public EntityHarmlessLightningBolt(World worldIn, double x, double y, double z, float amount) {
        super(worldIn, x, y, z, false);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = 2;
        this.amount = amount;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    public void onUpdate() {
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.lightningState == 2) {
            this.world.playSound(null, new BlockPos(this), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSound(null, new BlockPos(this), SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                --this.boltLivingTime;
                this.lightningState = 1;
            }
        }

        if (this.lightningState >= 0) {
            if (this.world.isRemote) {
                this.world.setLastLightningBolt(2);
            } else {
                List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX - 3.0D, this.posY - 3.0D, this.posZ - 3.0D, this.posX + 3.0D, this.posY + 6.0D + 3.0D, this.posZ + 3.0D));
                for (EntityLivingBase entity : list) {
                    if (!(entity instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer) entity, EnigmaticLegacy.thunderScroll) != -1)) {
                        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this)) {
                            entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, this.amount);
                        }
                    }
                }
            }
        }
    }
}
