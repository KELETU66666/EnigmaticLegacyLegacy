package keletu.enigmaticlegacy.util.loot;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import keletu.enigmaticlegacy.event.SuperpositionHandler;
import keletu.enigmaticlegacy.util.compat.CompatBubbles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Random;

public class SpecialLootModifierEndCity extends LootEntryTable {

    public SpecialLootModifierEndCity(ResourceLocation tableIn, int weightIn, int qualityIn, LootCondition[] conditionsIn, String entryName) {
        super(tableIn, weightIn, qualityIn, conditionsIn, entryName);
    }

    @Nonnull
    @Override
    public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
        WorldServer level = context.getWorld();
        Entity entity = context.getEntity(LootContext.EntityTarget.KILLER_PLAYER);

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!SuperpositionHandler.hasPersistentTag(player, "LootedAstralFruit") && SuperpositionHandler.hasCursed(player)) {
                SuperpositionHandler.setPersistentBoolean(player, "LootedAstralFruit", true);
                stacks.add(new ItemStack(CompatBubbles.astralFruit, 1));
            }
        }
    }

    @SubscribeEvent
    public static void lootLoad(LootTableLoadEvent evt) {
        String prefix = "minecraft:chests/";
        String name = evt.getName().toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            if (file.equals("end_city_treasure"))
                evt.getTable().addPool(getInjectPool(file));
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(new ResourceLocation(EnigmaticLegacy.MODID, "inject/spellstone_temple"))) {
            LootPool main = event.getTable().getPool("spellstones");
            main.addEntry(new LootEntryItem(CompatBubbles.ichorBottle, 65, 0, new LootFunction[0], new LootCondition[0], "enigmatic_inject_pool_special_ichor_bottle"));
        }
    }

    private static LootPool getInjectPool(String entryName) {
        return new LootPool(new LootEntry[]{getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(1), "enigmatic_inject_pool_special_1");
    }

    private static SpecialLootModifierEndCity getInjectEntry(String name, int weight) {
        return new SpecialLootModifierEndCity(new ResourceLocation(EnigmaticLegacy.MODID, "special/" + name), weight, 0, new LootCondition[0], "enigmatic_inject_pool_special_1");
    }
}