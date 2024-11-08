package keletu.enigmaticlegacy.client;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class ModelUltimatePendant extends ModelBase {

    protected ModelRenderer bipedBody;
    protected ModelRenderer gem;
    protected ItemStack stack;

    public ModelUltimatePendant(ItemStack stack) {
        textureWidth = 64;
        textureHeight = 32;
        this.stack = stack;

        bipedBody = new ModelRenderer(this, 0, 0);
        bipedBody.addBox(-8.5F, 0, -4, 17, 24, 8, 0);

        gem = new ModelRenderer(this, 50, 0);
        gem.addBox(1, -1, 0, 3, 3, 1, 0);
    }

    @Override
    public void render(Entity entity, float f, float f1, float ageInTicks, float f3, float f4, float scale) {
        GlStateManager.translate(0, -0.02F, 0);
        GlStateManager.scale(7 / 6F, 7 / 6F, 7 / 6F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        bipedBody.render(scale);

        GlStateManager.translate(-1.25 / 8F, 3 / 8F, -2.25 / 8F);

        if (stack.getItem() == EnigmaticLegacy.eldritchAmulet) {
            float lastLightmapX = OpenGlHelper.lastBrightnessX;
            float lastLightmapY = OpenGlHelper.lastBrightnessY;

            float light = 2 * (ageInTicks % 100);
            if (ageInTicks % 200 < 100) {
                light = 200 - light;
            }

            light += 40;

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
            gem.render(scale);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
        }else
            gem.render(scale);
    }
}