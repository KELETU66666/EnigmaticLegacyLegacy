package keletu.enigmaticlegacy.item;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.ELConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.helper.ExperienceHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ItemHeavenScroll extends ItemScrollBauble {

    public Map<EntityPlayer, Integer> flyMap = new WeakHashMap<EntityPlayer, Integer>();
    public final double baseXpConsumptionProbability = 0.025D / 2D;

    public ItemHeavenScroll(String name, EnumRarity rare) {
        super(name, rare);
        this.maxStackSize = 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<String> list, ITooltipFlag flagIn) {
        list.add("");

        if (GuiScreen.isShiftKeyDown()) {
            list.add(I18n.format("tooltip.enigmaticlegacy.heavenTome1"));
            list.add(I18n.format("tooltip.enigmaticlegacy.heavenTome2"));
            list.add(I18n.format("tooltip.enigmaticlegacy.heavenTome3"));
            list.add("");
            list.add(I18n.format("tooltip.enigmaticlegacy.heavenTome4"));
        } else {
            list.add(I18n.format("tooltip.enigmaticlegacy.holdShift"));
        }
    }

    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return BaublesApi.isBaubleEquipped((EntityPlayer) player, EnigmaticLegacy.heavenScroll) == -1 && super.canEquip(itemstack, player);
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase entityLivingBase) {
        if (entityLivingBase.world.isRemote)
            return;

        if (entityLivingBase instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLivingBase;

            if (Math.random() <= (this.baseXpConsumptionProbability * ELConfigs.flyingScrollXpCostModifier) && player.capabilities.isFlying) {
                ExperienceHelper.drainPlayerXP(player, 1);
            }

            this.handleFlight(player);
        }

    }

    protected void handleFlight(EntityPlayer player) {
        try {
            if (ExperienceHelper.getPlayerXP(player) > 0 && SuperpositionHandler.isInBeaconRange(player)) {

                if (!player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                }

                player.sendPlayerAbilities();
                this.flyMap.put(player, 100);

            } else if (this.flyMap.get(player) > 1) {
                this.flyMap.put(player, this.flyMap.get(player) - 1);
            } else if (this.flyMap.get(player) == 1) {
                if (!player.isCreative()) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                    player.sendPlayerAbilities();
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 100, true, false));
                }

                this.flyMap.put(player, 0);
            }

        } catch (NullPointerException ex) {
            this.flyMap.put(player, 0);
        }
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase entityLivingBase) {

        if (entityLivingBase instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLivingBase;

            if (!player.isCreative() && !player.isSpectator()) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }

            this.flyMap.put(player, 0);

        }
    }
}