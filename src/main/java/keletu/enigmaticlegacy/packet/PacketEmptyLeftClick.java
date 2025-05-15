package keletu.enigmaticlegacy.packet;

import io.netty.buffer.ByteBuf;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEmptyLeftClick implements IMessage {
    private boolean clicked;

    public PacketEmptyLeftClick() {
    }

    public PacketEmptyLeftClick(boolean clicked) {
        this.clicked = clicked;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.clicked = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(clicked);
    }

    public static Vec3d getAxisOffset(Vec3d axis, Vec3d offset) {
        Vec3d forward = axis.normalize();
        Vec3d side = axis.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d up = axis.crossProduct(side).normalize();
        return forward.scale(offset.x).add(side.scale(offset.z)).add(up.scale(offset.y));
    }

    public static class Handler implements IMessageHandler<PacketEmptyLeftClick, IMessage> {

        public IMessage onMessage(PacketEmptyLeftClick message, MessageContext ctx) {
            EntityPlayerMP servPlayer = ctx.getServerHandler().player;
            Vec3d center = getAxisOffset(servPlayer.getLookVec(), new Vec3d(1 * 1.5, 0, 0)).add(servPlayer.getPositionEyes(1));
            AxisAlignedBB box = new AxisAlignedBB(center.x, center.y, center.z, center.x, center.y, center.z).grow(1 * 0.5, 1, 1 * 0.5);
            float base = (float) (0.5F * servPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(servPlayer) * base;

            for (EntityLivingBase entitylivingbase : servPlayer.world.getEntitiesWithinAABB(EntityLivingBase.class, box.grow(1.0D, 0.25D, 1.0D))) {
                if (entitylivingbase != servPlayer && !servPlayer.isOnSameTeam(entitylivingbase) && servPlayer.getDistanceSq(entitylivingbase) < 9.0D) {
                    entitylivingbase.knockBack(servPlayer, 0.4F, MathHelper.sin(servPlayer.rotationYaw * 0.017453292F), -MathHelper.cos(servPlayer.rotationYaw * 0.017453292F));
                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(servPlayer), f3);
                }
            }

            servPlayer.world.playSound(null, servPlayer.posX, servPlayer.posY, servPlayer.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, servPlayer.getSoundCategory(), 1.0F, 1.0F);
            servPlayer.spawnSweepParticles();

            servPlayer.addExhaustion(0.1F);
            servPlayer.getCooldownTracker().setCooldown(EnigmaticLegacy.thunderScroll, (int) (16 / servPlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

            return null;
        }
    }
}