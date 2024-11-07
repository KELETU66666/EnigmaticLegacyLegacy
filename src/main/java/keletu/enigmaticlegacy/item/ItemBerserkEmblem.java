package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.render.IRenderBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import keletu.enigmaticlegacy.event.ELEvents;
import keletu.enigmaticlegacy.util.helper.IconHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemBerserkEmblem extends ItemBaseBauble implements IRenderBauble {


    public ItemBerserkEmblem() {
        super("berserk_emblem", EnumRarity.EPIC);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + attackDamage + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem1"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + attackSpeed + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem2"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + movementSpeed + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem3"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + damageResistance + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem4"));

            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.berserk_emblem5"));
            list.add(I18n.format("tooltip.enigmaticlegacy.berserk_emblem6"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }

        if (Minecraft.getMinecraft().player != null)
            if (BaublesApi.isBaubleEquipped(Minecraft.getMinecraft().player, this) != -1) {
                float missingPool = ELEvents.getMissingHealthPool(Minecraft.getMinecraft().player);
                int percentage = (int) (missingPool * 100F);

                list.add("");
                list.add(I18n.format("tooltip.enigmaticlegacy.berserk_emblem7"));
                list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + attackDamage * percentage + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem8"));
                list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + attackSpeed * percentage + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem9"));
                list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + movementSpeed * percentage + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem10"));
                list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.add") + damageResistance * percentage + "%" + I18n.format("tooltip.enigmaticlegacy.berserk_emblem11"));

            }

        list.add("");
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            living.getAttributeMap().applyAttributeModifiers(this.createAttributeMap(player));
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            living.getAttributeMap().removeAttributeModifiers(this.createAttributeMap((EntityPlayer) living));
        }
    }

    private Multimap<String, AttributeModifier> createAttributeMap(EntityPlayer player) {
        Multimap<String, AttributeModifier> attributesDefault = HashMultimap.create();

        float missingHealthPool = ELEvents.getMissingHealthPool(player);

        attributesDefault.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(UUID.fromString("ec62548c-5b26-401e-83fd-693e4aafa532"), "enigmaticlegacy:attack_speed_modifier", missingHealthPool * attackSpeed, 2));
        attributesDefault.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("f4ece564-d2c0-40d2-a96a-dc68b493137c"), "enigmaticlegacy:speed_modifier", missingHealthPool * movementSpeed, 2));

        return attributesDefault;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemIndestructible item = new EntityItemIndestructible(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;
        if (location instanceof EntityItem) {
            item.setThrower(((EntityItem) location).getThrower());
            item.setOwner(((EntityItem) location).getOwner());
        }

        return item;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, RenderType renderType, float v) {
            if(renderType == RenderType.BODY)
            {
                boolean armor = !entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
                Helper.rotateIfSneaking(entityPlayer);
                Helper.translateToChest();
                Helper.defaultTransforms();
                Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/berserk_emblem.png"));
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.4, -6 / 16D, armor ? -1 / 16D : 1 / 16D);
                GlStateManager.scale(0.25, 0.25, 0.25);
                IconHelper.renderIconIn3D(Tessellator.getInstance(), 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.1f);
            }

    }
}