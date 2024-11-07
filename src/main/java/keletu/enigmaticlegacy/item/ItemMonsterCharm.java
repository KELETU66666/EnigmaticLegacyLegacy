package keletu.enigmaticlegacy.item;

import baubles.api.BaubleType;
import baubles.api.render.IRenderBauble;
import static keletu.enigmaticlegacy.ELConfigs.*;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.util.helper.IconHelper;
import keletu.enigmaticlegacy.util.interfaces.ILootingBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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

public class ItemMonsterCharm extends ItemBaseBauble implements ILootingBonus, IRenderBauble {

    public float bonusXPModifier = 2.0F;

    public ItemMonsterCharm() {
        super("monster_charm", EnumRarity.RARE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {

        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.monsterCharm1") + Math.round(undeadDamageBonus * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.monsterCharm1_1"));
            list.add(TextFormatting.GOLD + I18n.format("tooltip.enigmaticlegacy.monsterCharm2") + Math.round(hostileDamageBonus * 100) + "%" + I18n.format("tooltip.enigmaticlegacy.monsterCharm2_1"));
            if (bonusLootingEnabled) {
                list.add(I18n.format("tooltip.enigmaticlegacy.monsterCharm3"));
            }
            if (doubleXPEnabled) {
                list.add("");
                list.add(I18n.format("tooltip.enigmaticlegacy.monsterCharm4"));
            }
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public int bonusLevelLooting() {
        return bonusLootingEnabled ? 1 : 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer entityPlayer, IRenderBauble.RenderType renderType, float v) {
        if(renderType == IRenderBauble.RenderType.BODY)
        {
            boolean armor = !entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
            IRenderBauble.Helper.rotateIfSneaking(entityPlayer);
            IRenderBauble.Helper.translateToChest();
            IRenderBauble.Helper.defaultTransforms();
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/monster_charm.png"));
            GlStateManager.rotate(180, 0, 1, 0);
            GlStateManager.translate(-0.4, -6 / 16D, armor ? -1 / 16D : 1 / 16D);
            GlStateManager.scale(0.25, 0.25, 0.25);
            IconHelper.renderIconIn3D(Tessellator.getInstance(), 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.1f);
        }

    }
}
