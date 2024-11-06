package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.render.IRenderBauble;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.entity.EntityItemIndestructible;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemAscensionAmulet extends ItemBaseBauble implements IRenderBauble {
    @SideOnly(Side.CLIENT)
    private static ModelBiped model;

    public ItemAscensionAmulet() {
        super("ascension_amulet", EnumRarity.EPIC);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase living) {
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            if (stack.getItem() != null) {
                AbstractAttributeMap map = player.getAttributeMap();
                map.applyAttributeModifiers(this.getCurrentModifiers(stack, player));
            }
        }

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
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            AbstractAttributeMap map = player.getAttributeMap();
            map.removeAttributeModifiers(this.getAllModifiers());
        }
    }

    public Multimap<String, AttributeModifier> getCurrentModifiers(ItemStack stack, EntityPlayer player) {
        Multimap<String, AttributeModifier> atts = HashMultimap.create();

        atts.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(UUID.fromString("f5bb82c7-0332-4adf-a414-2e4f03471983"), EnigmaticLegacy.MODID + ":attack_bonus", ELConfigs.enigmaticAmuletDamageBonus, 0));
        atts.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("cde98b8a-0cfc-45dc-929f-9cce9b6fbdfa"), EnigmaticLegacy.MODID + ":sprint_bonus", player.isSprinting() ? 0.15F : 0F, 2));
        atts.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(UUID.fromString("a4d4b794-a691-4757-b1cb-f5f2d5a25571"), EnigmaticLegacy.MODID + ":swim_bonus", 0.25F, 2));

        return atts;
    }

    public Multimap<String, AttributeModifier> getAllModifiers() {
        Multimap<String, AttributeModifier> atts = HashMultimap.create();

        atts.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(UUID.fromString("f5bb82c7-0332-4adf-a414-2e4f03471983"), EnigmaticLegacy.MODID + ":attack_bonus", ELConfigs.enigmaticAmuletDamageBonus, 0));
        atts.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("cde98b8a-0cfc-45dc-929f-9cce9b6fbdfa"), EnigmaticLegacy.MODID + ":sprint_bonus", 0.15F, 2));
        atts.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(UUID.fromString("a4d4b794-a691-4757-b1cb-f5f2d5a25571"), EnigmaticLegacy.MODID + ":swim_bonus", 0.25F, 2));

        return atts;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.AMULET;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, IRenderBauble.RenderType renderType, float v) {
        if (renderType == IRenderBauble.RenderType.BODY) {
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/layer/amulet_ascension.png"));
            IRenderBauble.Helper.rotateIfSneaking(entityPlayer);
            boolean armor = !entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
            GlStateManager.translate(0, 0, armor ? -0.075 : -0.02);

            float s = 1.05F / 16F;
            GlStateManager.scale(s, s, s);
            if (model == null)
                model = new ModelBiped();

            model.bipedBody.render(1F);
        }
    }
}
