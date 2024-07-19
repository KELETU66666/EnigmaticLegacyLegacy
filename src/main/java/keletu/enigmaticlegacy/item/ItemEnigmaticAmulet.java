package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemEnigmaticAmulet extends ItemBaseBauble {
    public ItemEnigmaticAmulet() {
        super("enigmatic_amulet", EnumRarity.RARE);
        this.setHasSubtypes(true);
        this.addPropertyOverride(new ResourceLocation("meta"), new IItemPropertyGetter() {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (stack.getMetadata() == 1) {
                    return 1;
                }
                if (stack.getMetadata() == 2) {
                    return 2;
                }
                if (stack.getMetadata() == 3) {
                    return 3;
                }
                if (stack.getMetadata() == 4) {
                    return 4;
                }
                if (stack.getMetadata() == 5) {
                    return 5;
                }
                if (stack.getMetadata() == 6) {
                    return 6;
                }
                if (stack.getMetadata() == 7) {
                    return 7;
                }
                return 0;
            }
        });
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTranslationKey(ItemStack item) {
        if (item.getItemDamage() == 0)
            return "item.unwitnessed_amulet";
        else return super.getTranslationKey();
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
    public void onUnequipped(ItemStack stack, EntityLivingBase living) {
        super.onUnequipped(stack, living);
        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) living;

            AbstractAttributeMap map = player.getAttributeMap();
            map.removeAttributeModifiers(this.getAllModifiers());
        }
    }

    public boolean ifHasColor(EntityPlayer player, int meta) {
        ItemStack stack = new ItemStack(this, 1, meta);
        return BaublesApi.isBaubleEquipped(player, this) != -1 && stack.getMetadata() == meta;
    }

    public Multimap<String, AttributeModifier> getCurrentModifiers(ItemStack stack, EntityPlayer player) {
        Multimap<String, AttributeModifier> atts = HashMultimap.create();

        int color = stack.getMetadata();

        if (color == 1) {
            atts.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(UUID.fromString("f5bb82c7-0332-4adf-a414-2e4f03471983"), EnigmaticLegacy.MODID + ":attack_bonus", ELConfigs.enigmaticAmuletDamageBonus, 0));
        } else if (color == 2) {
            atts.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(UUID.fromString("cde98b8a-0cfc-45dc-929f-9cce9b6fbdfa"), EnigmaticLegacy.MODID + ":sprint_bonus", player.isSprinting() ? 0.15F : 0F, 2));
        } else if (color == 7) {
            atts.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(UUID.fromString("a4d4b794-a691-4757-b1cb-f5f2d5a25571"), EnigmaticLegacy.MODID + ":swim_bonus", 0.25F, 2));
        }

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

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if (!worldIn.isRemote)
            if (getMetadata(stack) == 0) {
                setDamage(stack, worldIn.rand.nextInt(7) + 1);
            }
    }
}
