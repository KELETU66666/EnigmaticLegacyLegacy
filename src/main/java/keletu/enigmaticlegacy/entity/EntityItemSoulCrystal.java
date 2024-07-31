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
import keletu.enigmaticlegacy.core.ItemNBTHelper;
import static keletu.enigmaticlegacy.event.ELEvents.listEntityPos;
import keletu.enigmaticlegacy.item.ItemSoulCrystal;
import keletu.enigmaticlegacy.item.ItemStorageCrystal;
import keletu.enigmaticlegacy.packet.PacketRecallParticles;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityItemSoulCrystal extends Entity {

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItemSoulCrystal.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityItemSoulCrystal.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    private int age;
    private int pickupDelay;
    /** The health of this EntityItem. (For example, damage for tools) */
    private int health;
    private String thrower;
    private String owner;
    /** The EntityItem's random initial float height. */
    public float hoverStart;

    /**
     * The maximum age of this EntityItem.  The item is expired once this is reached.
     */
    public int lifespan = 6000;

    protected void entityInit() {
        this.dataManager.register(ITEM, ItemStack.EMPTY);
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
    }
    
    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    public EntityItemSoulCrystal(World world) {
        super(world);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
    }

    public EntityItemSoulCrystal(World world, double x, double y, double z) {
        super(world);
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D);
        this.motionY = 0.20000000298023224D;
        this.motionZ = (float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D);
    }

    public EntityItemSoulCrystal(World worldIn, double x, double y, double z, ItemStack stack)
    {
        this(worldIn, x, y, z);
        this.setItem(stack);
        this.lifespan = (stack.getItem() == null ? 6000 : stack.getItem().getEntityLifespan(stack, worldIn));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Health", (short)this.health);
        compound.setShort("Age", (short)this.age);
        compound.setShort("PickupDelay", (short)this.pickupDelay);
        compound.setInteger("Lifespan", lifespan);

        if (this.getThrower() != null)
        {
            compound.setString("Thrower", this.thrower);
        }

        if (this.getOwner() != null)
        {
            compound.setString("Owner", this.owner);
        }

        if (!this.getItem().isEmpty())
        {
            compound.setTag("Item", this.getItem().writeToNBT(new NBTTagCompound()));
        }

        if (this.getOwnerId() == null) {
            compound.setString("OwnerUUID", "");
        } else {
            compound.setString("OwnerUUID", this.getOwnerId().toString());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.health = compound.getShort("Health");
        this.age = compound.getShort("Age");

        if (compound.hasKey("PickupDelay"))
        {
            this.pickupDelay = compound.getShort("PickupDelay");
        }

        if (compound.hasKey("Owner"))
        {
            this.owner = compound.getString("Owner");
        }

        if (compound.hasKey("Thrower"))
        {
            this.thrower = compound.getString("Thrower");
        }

        NBTTagCompound nbttagcompound = compound.getCompoundTag("Item");
        this.setItem(new ItemStack(nbttagcompound));

        if (this.getItem().isEmpty())
        {
            this.setDead();
        }
        if (compound.hasKey("Lifespan")) lifespan = compound.getInteger("Lifespan");

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
        super.onUpdate();
        this.setGlowing(true);
        motionX *= 0.9;
        motionY *= 0.9;
        motionZ *= 0.9;
    }

    @Override
    public void onRemovedFromWorld() {
        EnigmaticLegacy.logger.warn("[WARN] Removing Permanent Item Entity: " + this);
        super.onRemovedFromWorld();
    }

    @Override
    public Entity changeDimension(int server, ITeleporter teleporter) {
        return null;
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    public String getOwner()
    {
        return this.owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getThrower()
    {
        return this.thrower;
    }

    public void setThrower(String thrower)
    {
        this.thrower = thrower;
    }

    @Nullable
    public UUID getOwnerId() {
        return (UUID) ((Optional<?>) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerId(@Nullable UUID p_184754_1_) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
    }

    /**
     * Gets the item that this entity represents.
     */
    public ItemStack getItem()
    {
        return this.getDataManager().get(ITEM);
    }

    /**
     * Sets the item that this entity represents.
     */
    public void setItem(ItemStack stack)
    {
        this.getDataManager().set(ITEM, stack);
        this.getDataManager().setDirty(ITEM);
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


            ItemStack clone = itemstack.copy();
            boolean isPlayerOwner = player.getUniqueID().equals(this.getOwnerId());
            boolean allowPickUp = false;

            if (item instanceof ItemStorageCrystal && (isPlayerOwner/* || !EnigmaticLegacy.enigmaticAmulet.isVesselOwnerOnly()*/)) {
                allowPickUp = true;
            } else if (item instanceof ItemSoulCrystal && isPlayerOwner) {
                allowPickUp = true;
            }

            if (allowPickUp) {

                if (item instanceof ItemStorageCrystal) {
                    NBTTagCompound crystalNBT = ItemNBTHelper.getNBT(itemstack);
                    ItemStack embeddedSoul = crystalNBT.hasKey("embeddedSoul") ? new ItemStack(crystalNBT.getCompoundTag("embeddedSoul")) : null;

                    if (!isPlayerOwner && embeddedSoul != null)
                        return;

                    EnigmaticLegacy.storageCrystal.retrieveDropsFromCrystal(itemstack, player, embeddedSoul);
					/*
					if (!isPlayerOwner && embeddedSoul != null) {
						PermanentItemEntity droppedSoulCrystal = new PermanentItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), embeddedSoul);
						droppedSoulCrystal.setOwnerId(this.getOwnerId());
						this.world.addEntity(droppedSoulCrystal);
					}
					 */

                } else if (item instanceof ItemSoulCrystal) {
                    listEntityPos.remove(this);
                    if (!EnigmaticLegacy.soulCrystal.retrieveSoulFromCrystal(player, itemstack))
                        return;

                }


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