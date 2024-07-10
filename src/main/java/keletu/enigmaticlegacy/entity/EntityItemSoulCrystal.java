/**
 * Thaumic Augmentation
 * Copyright (c) 2019 TheCodex6824.
 * <p>
 * This file is part of Thaumic Augmentation.
 * <p>
 * Thaumic Augmentation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Thaumic Augmentation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with Thaumic Augmentation.  If not, see <https://www.gnu.org/licenses/>.
 */

package keletu.enigmaticlegacy.entity;

import com.google.common.base.Optional;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.item.ItemSoulCrystal;
import keletu.enigmaticlegacy.packet.PacketRecallParticles;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityItemSoulCrystal extends EntityItem {

    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityItemSoulCrystal.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
    }


    public EntityItemSoulCrystal(World world) {
        super(world);
    }

    public EntityItemSoulCrystal(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityItemSoulCrystal(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        if (this.getOwnerId() == null) {
            compound.setString("OwnerUUID", "");
        } else {
            compound.setString("OwnerUUID", this.getOwnerId().toString());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        String s;

        if (compound.hasKey("OwnerUUID", 8)) {
            s = compound.getString("OwnerUUID");
        } else {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerId(UUID.fromString(s));
            } catch (Throwable ignored) {

            }
        }
    }

    @Override
    public void onUpdate() {
        this.setNoDespawn();
        this.setGlowing(true);
        //motionX *= 0.9;
        //motionY *= 0.9;
        //motionZ *= 0.9;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Nullable
    public UUID getOwnerId() {
        return (UUID) ((Optional<?>) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.world.isRemote) {
            //if (this.pickupDelay > 0) {
            //    return;
            //}

            ItemStack itemstack = this.getItem();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();

            int hook = ForgeEventFactory.onItemPickup(this, player);
            if (hook < 0) {
                return;
            }


            ItemStack clone = itemstack.copy();
            boolean isPlayerOwner = player.getUniqueID().equals(this.getOwnerId());
            boolean allowPickUp = item instanceof ItemSoulCrystal && isPlayerOwner;

            if (allowPickUp) {

                if (!EnigmaticLegacy.soulCrystal.retrieveSoulFromCrystal(player, itemstack))
                    return;

                EnigmaticLegacy.packetInstance.sendToAllAround(new PacketRecallParticles(this.posX, this.posY + (this.getEyeHeight() / 2), this.posZ, 48, false), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), this.posX, this.posY + (this.getEyeHeight() / 2), this.posZ, 64));

                player.addStat(StatList.getObjectsPickedUpStats(item), i);
                //CursedRingMod.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(this.getX(), this.getY(), this.getZ(), 64, this.level.dimension())), new PacketHandleItemPickup(player.getId(), this.getId()));

                this.setDead();
                itemstack.setCount(0);

            }
        }

    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

}