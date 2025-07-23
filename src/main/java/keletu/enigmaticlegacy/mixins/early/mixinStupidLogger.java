package keletu.enigmaticlegacy.mixins.early;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SharedMonsterAttributes.class)
public abstract class mixinStupidLogger {

    @Shadow
    private static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound) {}

    @Inject(method = "setAttributeModifiers", at = @At(value = "HEAD"), cancellable = true)
    private static void setAttributeModifiers(AbstractAttributeMap map, NBTTagList list, CallbackInfo ci)
    {
        for (int i = 0; i < list.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
            IAttributeInstance iattributeinstance = map.getAttributeInstanceByName(nbttagcompound.getString("Name"));

            if (iattributeinstance != null)
            {
                applyModifiersToAttributeInstance(iattributeinstance, nbttagcompound);
            }
        }

        ci.cancel();
    }
}
