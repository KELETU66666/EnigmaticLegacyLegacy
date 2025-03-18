package keletu.enigmaticlegacy.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderEntitySoulCrystal extends Render<EntityItemSoulCrystal> {
    private final RenderItem itemRenderer;
    private final Random random = new Random();

    public RenderEntitySoulCrystal(RenderManager p_i46167_1_, RenderItem p_i46167_2_) {
        super(p_i46167_1_);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int transformModelCount(EntityItemSoulCrystal p_transformModelCount_1_, double p_transformModelCount_2_, double p_transformModelCount_4_, double p_transformModelCount_6_, float p_transformModelCount_8_, IBakedModel p_transformModelCount_9_) {
        ItemStack itemstack = p_transformModelCount_1_.getItem();
        Item item = itemstack.getItem();
        if (item == null) {
            return 0;
        } else {
            boolean flag = p_transformModelCount_9_.isGui3d();
            int i = this.getModelCount(itemstack);
            float f = 0.25F;
            float f1 = this.shouldBob() ? MathHelper.sin((p_transformModelCount_8_) / 10.0F + p_transformModelCount_1_.hoverStart) * 0.1F + 0.1F : 0.0F;
            float f2 = p_transformModelCount_9_.getItemCameraTransforms().getTransform(TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_transformModelCount_2_, (float)p_transformModelCount_4_ + f1 + 0.25F * f2, (float)p_transformModelCount_6_);
            if (flag || this.renderManager.options != null) {
                float f3 = ((p_transformModelCount_8_) / 20.0F + p_transformModelCount_1_.hoverStart) * 57.295776F;
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    protected int getModelCount(ItemStack p_getModelCount_1_) {
        int i = 1;
        if (p_getModelCount_1_.getCount() > 48) {
            i = 5;
        } else if (p_getModelCount_1_.getCount() > 32) {
            i = 4;
        } else if (p_getModelCount_1_.getCount() > 16) {
            i = 3;
        } else if (p_getModelCount_1_.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    public void doRender(EntityItemSoulCrystal p_doRender_1_, double p_doRender_2_, double p_doRender_4_, double p_doRender_6_, float p_doRender_8_, float p_doRender_9_) {
        ItemStack itemstack = p_doRender_1_.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        this.random.setSeed(i);
        boolean flag = false;
        if (this.bindEntityTexture(p_doRender_1_)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(p_doRender_1_)).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, p_doRender_1_.world, null);
        int j = this.transformModelCount(p_doRender_1_, p_doRender_2_, p_doRender_4_, p_doRender_6_, p_doRender_9_, ibakedmodel);
        boolean flag1 = ibakedmodel.isGui3d();
        float f7;
        float f9;
        if (!flag1) {
            float f3 = -0.0F * (float)(j - 1) * 0.5F;
            f7 = -0.0F * (float)(j - 1) * 0.5F;
            f9 = -0.09375F * (float)(j - 1) * 0.5F;
            GlStateManager.translate(f3, f7, f9);
        }

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(p_doRender_1_));
        }

        //for(int k = 0; k < j; ++k) {
            IBakedModel transformedModel;
            if (flag1) {
                GlStateManager.pushMatrix();
                //if (k > 0) {
                    f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(this.shouldSpreadItems() ? f7 : 0.0F, this.shouldSpreadItems() ? f9 : 0.0F, f6);
                //}

                transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
            } else {
                GlStateManager.pushMatrix();
                //if (k > 0) {
                    f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    GlStateManager.translate(f7, f9, 0.0F);
                //}

                transformedModel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, TransformType.GROUND, false);
                this.itemRenderer.renderItem(itemstack, transformedModel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.09375F);
            }
       // }

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(p_doRender_1_);
        if (flag) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(p_doRender_1_)).restoreLastBlurMipmap();
        }

        super.doRender(p_doRender_1_, p_doRender_2_, p_doRender_4_, p_doRender_6_, p_doRender_8_, p_doRender_9_);
    }

    protected ResourceLocation getEntityTexture(EntityItemSoulCrystal p_getEntityTexture_1_) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public boolean shouldSpreadItems() {
        return true;
    }

    public boolean shouldBob() {
        return true;
    }
}