package keletu.enigmaticlegacy.util.loot;

import keletu.enigmaticlegacy.EnigmaticLegacy;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class LootHandlerForgottenRelics {

    private static final String TABLE = "inject/nether_bridge";

    public LootHandlerForgottenRelics() {
            LootTableList.register(new ResourceLocation(EnigmaticLegacy.MODID, TABLE));
    }

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent evt) {
        String prefix = "minecraft:chests/";
        String name = evt.getName().toString();

        if (name.startsWith(prefix)) {
            String file = name.substring(name.indexOf(prefix) + prefix.length());
            if (file.equals("nether_bridge")) {
                evt.getTable().addPool(getInjectPool(file));
            }
        }
    }

    private LootPool getInjectPool(String entryName) {
        return new LootPool(new LootEntry[]{getInjectEntry(entryName, 1)}, new LootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "enigmatic_inject_pool_bridge");
    }

    private LootEntryTable getInjectEntry(String name, int weight) {
        return new LootEntryTable(new ResourceLocation(EnigmaticLegacy.MODID, "inject/" + name), weight, 0, new LootCondition[0], "enigmatic_inject_pool_bridge");
    }

}