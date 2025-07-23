package keletu.enigmaticlegacy.client;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.util.compat.CompatBubbles;
import keletu.enigmaticlegacy.util.compat.ModCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerAmulet extends LayerBauble {
    private static final ResourceLocation FLAME_TEXTURE = new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/layer/amulet_ascension.png");
    private static final ResourceLocation THORN_TEXTURE = new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/layer/amulet_eldritch.png");

    public LayerAmulet(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Override
    protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderChest(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void renderChest(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ModelBase amulet = setTexturesGetModel(player);
        if (amulet == null) return;

        if (player.isSneaking()) GlStateManager.translate(0, 0.2F, 0);
        modelPlayer.bipedBody.postRender(scale);
        amulet.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
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
        ItemStack stack;
        if (ModCompat.COMPAT_BUBBLES)
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(CompatBubbles.LayerAmuletItems(BaubleType.AMULET, player));
        else
            stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.AMULET.getValidSlots()[0]);

        if (BaublesApi.isBaubleEquipped(player, stack.getItem()) == -1) return null;
        ResourceLocation textures = getTextures(stack);
        if (textures != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
            return new ModelAmulet(stack);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getRenderTexture(ItemStack stack) {
        int color = stack.getMetadata();
        String x;
        switch (color) {
            case 1:
                x = "red";
                break;
            case 2:
                x = "aqua";
                break;
            case 3:
                x = "violet";
                break;
            case 4:
                x = "magenta";
                break;
            case 5:
                x = "green";
                break;
            case 6:
                x = "black";
                break;
            case 7:
                x = "blue";
                break;
            default:
                x = "red";
        }
        return new ResourceLocation(EnigmaticLegacy.MODID, "textures/models/layer/amulet_" + x + ".png");
    }

    private @Nullable ResourceLocation getTextures(ItemStack stack) {
        if (stack.getItem() == EnigmaticLegacy.enigmaticAmulet) return getRenderTexture(stack);
        else if (stack.getItem() == EnigmaticLegacy.ascensionAmulet) return FLAME_TEXTURE;
        else if (stack.getItem() == EnigmaticLegacy.eldritchAmulet) return THORN_TEXTURE;
        else return null;
    }
}