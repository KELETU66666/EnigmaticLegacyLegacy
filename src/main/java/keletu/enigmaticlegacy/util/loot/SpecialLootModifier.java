package keletu.enigmaticlegacy.util.loot;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Random;

@Mod.EventBusSubscriber(modid = EnigmaticLegacy.MODID)
public class SpecialLootModifier extends LootEntryTable {

    protected SpecialLootModifier(ResourceLocation tableIn, int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
        super(tableIn, weightIn, qualityIn, conditionsIn, entryName);
    }

    @Nonnull
    @Override
    public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
        WorldServer level = context.getWorld();
        Entity entity = context.getEntity(LootContext.EntityTarget.KILLER_PLAYER);

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!SuperpositionHandler.hasPersistentTag(player, "LootedArchitectEye")) {
                SuperpositionHandler.setPersistentBoolean(player, "LootedArchitectEye", true);
                stacks.add(new ItemStack(EnigmaticLegacy.enigmaticEye, 1));
            }
        }
    }

    @SubscribeEvent
    public static void lootLoad(LootTableLoadEvent evt) {
        String prefix = "minecraft:chests/";
        String name = evt.getName().toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            evt.getTable().addPool(getInjectPool(file));
        }
    }

    private static LootPool getInjectPool(String entryName) {
        return new LootPool(new LootEntry[]{getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(1), "enigmatic_inject_pool_special");
    }

    private static SpecialLootModifier getInjectEntry(String name, int weight) {
        return new SpecialLootModifier(new ResourceLocation(EnigmaticLegacy.MODID, "special/" + name), weight, 0, new LootCondition[0], "enigmatic_inject_pool_special");
    }
}