package keletu.enigmaticlegacy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ELConfigs {

    public static float painMultiplier;
    public static float monsterDamageDebuff;
    public static float armorDebuff;
    public static float experienceBonus;
    public static int fortuneBonus;
    public static int lootingBonus;
    public static int enchantingBonus;

    public static float knockbackDebuff;
    public static double neutralAngerRange;
    public static double neutralXRayRange;
    public static double endermenRandomportRange;
    public static double endermenRandomportFrequency;
    public static boolean saveTheBees;
    public static Boolean enableSpecialDrops;
    public static int iconOffset;
    public static int heartLoss;
    public static boolean ultraHardcore;
    public static int range;
    public static int superRange;
    public static boolean invertShift;

    public static final List<ResourceLocation> neutralAngerBlacklist = new ArrayList<>();
    public static final List<ResourceLocation> cursedItemList = new ArrayList<>();


    public static void onConfig(FMLPreInitializationEvent builder) {
        Configuration config = new Configuration(builder.getSuggestedConfigurationFile());

        painMultiplier = config.getFloat("PainModifier", "Generic Config", 2F, 0, 100, "Defines how much damage bearers of the ring receive from any source. Measured as percentage.");

        monsterDamageDebuff = config.getFloat("MonsterDamageDebuff", "Generic Config", 0.5F, 0, 100, "How much damage monsters receive from bearers of the ring will be decreased, in percents.");

        armorDebuff = config.getFloat("ArmorDebuff", "Generic Config", 0.3F, 0, 1, "How much less effective armor will be for those who bear the ring. Measured as percetage.");

        experienceBonus = config.getFloat("ExperienceBonus", "Generic Config", 4F, 0, 100, "How much experience will drop from mobs to bearers of the ring, measured in percents.");

        fortuneBonus = config.getInt("FortuneBonus", "Generic Config", 1, 0, 100, "How many bonus Fortune levels ring provides");

        lootingBonus = config.getInt("LootingBonus", "Generic Config", 1, 0, 100, "How many bonus Looting levels ring provides");

        enchantingBonus = config.getInt("EnchantingBonus", "Generic Config", 10, 0, 100, "How much additional Enchanting Power ring provides in Enchanting Table.");
        
        enableSpecialDrops = config.getBoolean("EnableSpecialDrops", "Generic Config", true, "Set to false to disable ALL special drops that can be obtained from vanilla mobs when "
                + "bearing Ring of the Seven Curses.");

        ultraHardcore = config.getBoolean("UltraHardcode", "Generic Config", false, "If true, Ring of the Seven Curses will be equipped into player's ring slot right away when "
                + "entering a new world, instead of just being added to their inventory.");

        knockbackDebuff = config.getFloat("KnockbackDebuff", "Generic Config", 2F, 0, 1, "How much knockback bearers of the ring take, measured in percents.");

        neutralAngerRange = config.getFloat("NeutralAngerRange", "Generic Config", 24, 4, 100, "Range in which neutral creatures are angered against bearers of the ring.");

        neutralXRayRange = config.getFloat("NeutralXRayRange", "Generic Config", 4, 0, 100, "Range in which neutral creatures can see and target bearers of the ring even if they can't directly see them.");

        endermenRandomportFrequency = config.getFloat("EndermenRandomportFrequency", "Generic Config", 1, 0.01F, 100, "Allows to adjust how frequently Endermen will try to randomly teleport to player bearing the ring, even "
                        + "if they can't see the player and are not angered yet. Lower value = less probability of this happening.");

        endermenRandomportRange = config.getFloat("EndermenRandomportRange", "Generic Config", 32, 8, 100, "Range in which Endermen can try to randomly teleport to bearers of the ring.");

        iconOffset = config.getInt("IconOffset", "Generic Config", -51, -500, 500, "X Offset for EnderChest tab");

        heartLoss = config.getInt("MaxHeartLoss", "Generic Config", 7, 0, 9, "Max amount of heart loss");

        range = config.getInt("Range", "MagnetRing", 8, 1, 256, "The radius in which Magnetic Ring will attract items.");

        invertShift = config.getBoolean("InvertShift", "MagnetRing", false, "Inverts the Shift behaviour of Magnetic Ring and Dislocation Ring.");

        superRange = config.getInt("Range", "SuperMagnetRing", 16, 1, 256, "The radius in which Dislocation Ring will attract items.");

        //builder.pushCategory("Save the Bees", "This category exists solely because of Jusey1z who really wanted to protect his bees."
        //        + Configuration.NEW_LINE + "Btw Jusey, when I said 'very cute though', I meant you. Bees are cute either of course.");

        //saveTheBees = builder
        //        .comment("If true, bees will never affected by the Second Curse of Ring of the Seven Curses.")
        //        .getBoolean("DontTouchMyBees", false);

        // Ugly but gets the job done

        neutralAngerBlacklist.clear();
        String[] blacklist = config.getStringList("CursedRingNeutralAngerBlacklist", "The Seven Curses", new String[]{"minecraft:ocelot", "minecraft:snowman"}, "List of entities that should never be affected"
                + " by the Second Curse of Ring of the Seven Curses. Examples: minecraft:villager_golem, minecraft:wolf. Changing this option required game restart to take effect.");

        Arrays.stream(blacklist).forEach(entry -> neutralAngerBlacklist.add(new ResourceLocation(entry)));

        cursedItemList.clear();
        String[] cursed = config.getStringList("ItemBeCursed", "The Seven Curses", new String[0], "List of item needs ware ring to use"
                + "Examples: minecraft:dirt, minecraft:diamond_sword. Changing this option required game restart to take effect.");

        Arrays.stream(cursed).forEach(entry -> cursedItemList.add(new ResourceLocation(entry)));


        config.save();
    }

}
