package keletu.enigmaticlegacy.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelAmulet extends ModelBase {

    protected ModelRenderer boxChain;
    protected ModelRenderer boxGem;

    public ModelAmulet() {
        this.textureWidth = 16;
        this.textureHeight = 16;

        this.boxGem = new ModelRenderer(this, 16, 16);
        this.boxGem.addBox(-0.5F, 3, -4.5F, 1, 1, 1, 0);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        GlStateManager.translate(0, -0.02, 0);
        GlStateManager.scale(7/6F, 7/6F, 7/6F);
        this.boxChain.render(scale);
        GlStateManager.scale(1, 1, 0.5F);
        this.boxGem.render(scale);
    }
}