package keletu.enigmaticlegacy.client;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.util.interfaces.IScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerScroll {

    @SubscribeEvent
    public void renderLivingPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (this.shouldRender(event.getEntity())) {
            this.render(event.getEntity(), event.getX(), event.getY(), event.getZ(), event.getPartialRenderTick());
        }
    }

    public boolean shouldRender(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) entity);

            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack equipped = baubles.getStackInSlot(slot);
                if (!equipped.isEmpty() && equipped.getItem() instanceof IScroll) {
                    return BaublesApi.isBaubleEquipped((EntityPlayer) entity, equipped.getItem()) != -1;
                }
            }
        }
        return false;
    }

    public void render(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(0.3, 0.3, 0.3);
        GlStateManager.translate(0, 3.75, 0);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0f);
        if (entity instanceof EntityPlayer) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) entity);

            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack equipped = baubles.getStackInSlot(slot);
                if (!equipped.isEmpty() && equipped.getItem() instanceof IScroll) {
                    renderScroll(entity, partialTicks, equipped);
                }
            }
        }
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderScroll(EntityLivingBase entity, float partialTicks, ItemStack scroll) {
        float rotateAngleY = (entity.ticksExisted + partialTicks) / 5.0F;
        //float rotateAngleZ = MathHelper.cos((entity.ticksExisted + partialTicks) / 5.0F) / 4.0F;

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(scroll, entity.world, entity);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();

        GlStateManager.rotate(rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);

        // Move the draw away from the entity being drawn around
        GlStateManager.translate(0F, 0F, 2.5F);

        Minecraft.getMinecraft().getRenderItem().renderItem(scroll, ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false));

        GlStateManager.popMatrix();
    }
}