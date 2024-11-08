package keletu.enigmaticlegacy.client;

import baubles.common.Config;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import javax.annotation.Nonnull;

public abstract class LayerBauble implements LayerRenderer<EntityPlayer> {

    protected RenderPlayer renderPlayer;
    protected ModelPlayer modelPlayer;
    protected boolean slim;

    public LayerBauble(RenderPlayer renderPlayer) {
        this(renderPlayer, false);
    }

    public LayerBauble(RenderPlayer renderPlayer, boolean slim) {
        this.renderPlayer = renderPlayer;
        this.modelPlayer = renderPlayer.getMainModel();
        this.slim = slim;
    }

    @Override
    public final void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(!Config.renderBaubles || player.getActivePotionEffect(MobEffects.INVISIBILITY) != null) return;

        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();

        GlStateManager.pushMatrix();
        renderLayer(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.popMatrix();
    }

    protected abstract void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}