package keletu.enigmaticlegacy.client;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.item.ItemBerserkEmblem;
import keletu.enigmaticlegacy.item.ItemEnigmaticEye;
import keletu.enigmaticlegacy.item.ItemMiningCharm;
import keletu.enigmaticlegacy.item.ItemMonsterCharm;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LayerCharm extends LayerBauble {
    private static final ResourceLocation MONSTER_CHARM = new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/monster_charm.png");
    private static final ResourceLocation MINING_CHARM = new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/mining_charm.png");
    private static final ResourceLocation BERSERK_EMBLEM = new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/berserk_emblem.png");
    private static final ResourceLocation ENIGMATIC_EYE = new ResourceLocation(EnigmaticLegacy.MODID, "textures/items/enigmatic_eye.png");

    public LayerCharm(RenderPlayer renderPlayer) {
        super(renderPlayer);
    }

    @Override
    protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderChest(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void renderChest(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ModelBase amulet = setTexturesGetModel(player);
        if (amulet == null) return;

        if (player.isSneaking())
            GlStateManager.translate(0, 0.2F, 0);
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

    public static int LayerCharmItems(BaubleType type, EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            if (baubles.getSlotType(i) != type) continue;
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack.getItem() instanceof ItemMonsterCharm) return i;
            if (stack.getItem() instanceof ItemMiningCharm) return i;
            if (stack.getItem() instanceof ItemBerserkEmblem) return i;
            if (stack.getItem() instanceof ItemEnigmaticEye) return i;
        }
        return -1;
    }

    private ModelBase setTexturesGetModel(EntityPlayer player) {
        ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(LayerCharmItems(BaubleType.CHARM, player));
        if (BaublesApi.isBaubleEquipped(player, stack.getItem()) == -1) return null;
        ResourceLocation textures = getTextures(stack);
        if (textures != null) {
            return new ModelCharm(textures, stack);
        }
        return null;
    }

    private @Nullable ResourceLocation getTextures(ItemStack stack) {
        if (stack.getItem() == EnigmaticLegacy.monsterCharm) return MONSTER_CHARM;
        else if (stack.getItem() == EnigmaticLegacy.miningCharm) return MINING_CHARM;
        else if (stack.getItem() == EnigmaticLegacy.berserkEmblem) return BERSERK_EMBLEM;
        else if (stack.getItem() == EnigmaticLegacy.enigmaticEye) return ENIGMATIC_EYE;
        else return null;
    }
}