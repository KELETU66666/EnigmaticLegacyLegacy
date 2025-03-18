package keletu.enigmaticlegacy.client;

import baubles.api.render.IRenderBauble;
import keletu.enigmaticlegacy.item.ItemEnigmaticEye;
import keletu.enigmaticlegacy.util.helper.IconHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModelCharm extends ModelBase {

    ResourceLocation path;
    ItemStack stack;
    public static TextureAtlasSprite textureAtlasEye;

    public ModelCharm(ResourceLocation path, ItemStack stack) {
        this.textureWidth = 16;
        this.textureHeight = 16;
        this.path = path;
        this.stack = stack;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            boolean armor = !entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty();
            IRenderBauble.Helper.translateToChest();
            IRenderBauble.Helper.defaultTransforms();
            if (stack.getItem() instanceof ItemEnigmaticEye) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.4, -6 / 16D, armor ? -1 / 16D : 1 / 16D);
                GlStateManager.scale(0.25, 0.25, 0.25);

                TextureAtlasSprite gemIcon = textureAtlasEye;
                float f5 = gemIcon.getMinU();
                float f6 = gemIcon.getMaxU();
                float f7 = gemIcon.getMinV();
                float f8 = gemIcon.getMaxV();
                IconHelper.renderIconIn3D(Tessellator.getInstance(), f6, f7, f5, f8, gemIcon.getIconWidth(), gemIcon.getIconHeight(), 0.1F);
            } else {
                Minecraft.getMinecraft().renderEngine.bindTexture(path);
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.4, -6 / 16D, armor ? -1 / 16D : 1 / 16D);
                GlStateManager.scale(0.25, 0.25, 0.25);
                IconHelper.renderIconIn3D(Tessellator.getInstance(), 0.0f, 0.0f, 1.0f, 1.0f, 256, 256, 0.1f);
            }
        }
    }
}