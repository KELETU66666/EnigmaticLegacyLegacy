package keletu.enigmaticlegacy.client;

import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticConfigs;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerHeadwear extends LayerBauble {
    private static final ResourceLocation OMINOUS_MASK = new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/layer/half_heart_mask.png");

    public LayerHeadwear(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Override
    protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderHeadwear(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void renderHeadwear(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ModelBase headwear = setTexturesGetModel(player);
        if (headwear == null) return;

        if (player.isSneaking())
            GlStateManager.translate(0, 0.2F, 0);
        modelPlayer.bipedHead.postRender(scale);
        headwear.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public static boolean shouldRenderInSlot(EntityPlayer player, EntityEquipmentSlot slot) {
        ItemStack stack = player.getItemStackFromSlot(slot);
        return stack.isEmpty() ||
                (stack.getTagCompound() != null &&
                        stack.getTagCompound().getBoolean("classy_hat_invisible") &&
                        stack.getTagCompound().getCompoundTag("classy_hat_disguise").isEmpty());
    }

    public static boolean shouldItemStackRender(EntityPlayer player, ItemStack stack) {
        return stack.getTagCompound() == null || !stack.getTagCompound().getBoolean("phantom_thread_invisible");
    }

    private ModelBase setTexturesGetModel(EntityPlayer player) {
        if (EnigmaticConfigs.allowAddonItems) {
            if (BaublesApi.isBaubleEquipped(player, EnigmaticLegacy.halfHeartMask) != -1) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(OMINOUS_MASK);

                return new ModelHeadBauble();
            }
        }
        return null;
    }

    private @Nullable ResourceLocation getTextures(ItemStack stack) {
        if (stack.getItem() == EnigmaticLegacy.halfHeartMask) return OMINOUS_MASK;
        else return null;
    }
}