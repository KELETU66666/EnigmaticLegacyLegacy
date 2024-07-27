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
    public static int xIconOffsetBauble;
    public static int yIconOffsetBauble;
    public static int heartLoss;
    public static boolean ultraNoobMode;
    public static boolean ultraHardcore;
    public static int range;
    public static int superRange;
    public static boolean invertShift;
    public static float breakSpeedBonus;
    public static double reachDistanceBonus;
    public static float undeadDamageBonus;
    public static float hostileDamageBonus;
    public static boolean bonusLootingEnabled;
    public static boolean doubleXPEnabled;
    public static double attackDamage;
    public static double attackSpeed;
    public static double movementSpeed;
    public static double damageResistance;
    public static float twistAttackDamage;
    public static float twistAttackSpeed;
    public static float bossDamageBonus;
    public static float knockbackBonus;
    public static float xpCollectionRange;
    public static int radius;
    public static boolean spawnWithBook;
    public static boolean enableWitherite;
    public static boolean useWhitelist;
    public static float cursedScrollDamageBoost;
    public static float cursedScrollMiningBoost;
    public static float cursedScrollRegenBoost;

    public static float infinitumAttackDamage;
    public static float infinitumAttackSpeed;
    public static float infinitumBossDamageBonus;
    public static float infinitumKnockbackBonus;
    public static float infinitumLifestealBonus;
    public static float infinitumUndeadProbability;
    public static float enigmaticAmuletDamageBonus;
    public static boolean vesselEnabled;

    public static double defaultArmorBonus;
    public static double superArmorBonus;
    public static double superArmorToughnessBonus;
    public static float knockbackResistance;
    public static float meleeResistance;
    public static float explosionResistance;
    public static double vulnerabilityModifier;

    public static final List<ResourceLocation> neutralAngerBlacklist = new ArrayList<>();
    public static final List<ResourceLocation> neutralAngerWhitelist = new ArrayList<>();
    public static final List<ResourceLocation> cursedItemList = new ArrayList<>();
    public static final List<ResourceLocation> eldritchItemList = new ArrayList<>();

    public static int oceanStoneSpellstoneCooldown;
    public static float oceanStoneSwimmingSpeedBoost;
    public static float oceanStoneUnderwaterCreaturesResistance;
    public static double oceanStoneXpCostModifier;

    public static void onConfig(FMLPreInitializationEvent builder) {
        Configuration config = new Configuration(builder.getSuggestedConfigurationFile());

        spawnWithBook = config.getBoolean("SpawnWithBook", "Generic Config", true, "If true, When player entering a new world will give the acknowledgment guidebook");

        enableWitherite = config.getBoolean("EnableWitherite", "Generic Config", true, "If true, wither will drop witherite ingot (with ore 'ingotNetherite') when killed by player, you can disable it if your pack have other ways to get netherite");

        painMultiplier = config.getFloat("PainModifier", "The Seven Curses", 2F, 0, 100, "Defines how much damage bearers of the ring receive from any source. Measured as percentage.");

        monsterDamageDebuff = config.getFloat("MonsterDamageDebuff", "The Seven Curses", 0.5F, 0, 1, "How much damage monsters receive from bearers of the ring will be decreased, in percents.");

        armorDebuff = config.getFloat("ArmorDebuff", "The Seven Curses", 0.3F, 0, 1, "How much less effective armor will be for those who bear the ring. Measured as percetage.");

        experienceBonus = config.getFloat("ExperienceBonus", "The Seven Curses", 4F, 0, 100, "How much experience will drop from mobs to bearers of the ring, measured in percents.");

        fortuneBonus = config.getInt("FortuneBonus", "The Seven Curses", 1, 0, 100, "How many bonus Fortune levels ring provides");

        lootingBonus = config.getInt("LootingBonus", "The Seven Curses", 1, 0, 100, "How many bonus Looting levels ring provides");

        enchantingBonus = config.getInt("EnchantingBonus", "The Seven Curses", 10, 0, 100, "How much additional Enchanting Power ring provides in Enchanting Table.");

        useWhitelist = config.getBoolean("UseWhitelist", "The Seven Curses", false, "If true, use whitelist for cursed ring");

        enableSpecialDrops = config.getBoolean("EnableSpecialDrops", "The Seven Curses", true, "Set to false to disable ALL special drops that can be obtained from vanilla mobs when "
                + "bearing Ring of the Seven Curses.");

        ultraNoobMode = config.getBoolean("UltraNoobMode", "The Seven Curses", false, "If true, Player's will not received Ring of Seven Curses when entering a new world.");

        ultraHardcore = config.getBoolean("UltraHardcore", "The Seven Curses", false, "If true, Ring of the Seven Curses will be equipped into player's ring slot right away when "
                + "entering a new world, instead of just being added to their inventory.");

        knockbackDebuff = config.getFloat("KnockbackDebuff", "The Seven Curses", 2F, 0, 1, "How much knockback bearers of the ring take, measured in percents.");

        neutralAngerRange = config.getFloat("NeutralAngerRange", "The Seven Curses", 24, 4, 100, "Range in which neutral creatures are angered against bearers of the ring.");

        neutralXRayRange = config.getFloat("NeutralXRayRange", "The Seven Curses", 4, 0, 100, "Range in which neutral creatures can see and target bearers of the ring even if they can't directly see them.");

        endermenRandomportFrequency = config.getFloat("EndermenRandomportFrequency", "The Seven Curses", 1, 0.01F, 100, "Allows to adjust how frequently Endermen will try to randomly teleport to player bearing the ring, even "
                + "if they can't see the player and are not angered yet. Lower value = less probability of this happening.");

        endermenRandomportRange = config.getFloat("EndermenRandomportRange", "The Seven Curses", 32, 8, 100, "Range in which Endermen can try to randomly teleport to bearers of the ring.");

        iconOffset = config.getInt("IconOffset", "The Seven Curses", -51, -500, 500, "X Offset for EnderChest tab");

        xIconOffsetBauble = config.getInt("IconOffset", "Special Baubles", 0, -500, 500, "X Offset for Enigmatic Baubles tab");

        yIconOffsetBauble = config.getInt("IconOffset", "Special Baubles", 0, -500, 500, "Y Offset for Enigmatic Baubles tab");

        heartLoss = config.getInt("MaxHeartLoss", "The Seven Curses", 7, 0, 9, "Max amount of heart loss");

        range = config.getInt("Range", "MagnetRing", 8, 1, 256, "The radius in which Magnetic Ring will attract items.");

        invertShift = config.getBoolean("InvertShift", "MagnetRing", false, "Inverts the Shift behaviour of Magnetic Ring and Dislocation Ring.");

        superRange = config.getInt("Range", "SuperMagnetRing", 16, 1, 256, "The radius in which Dislocation Ring will attract items.");

        breakSpeedBonus = config.getFloat("BreakSpeed", "MiningCharm", 0.3F, 0, 10, "Mining speed boost granted by Charm of Treasure Hunter. Defined as percentage.");

        reachDistanceBonus = config.getFloat("ReachDistance", "MiningCharm", 2.15F, 0, 16, "Additional block reach granted by Charm of Treasure Hunter.");

        undeadDamageBonus = config.getFloat("UndeadDamage", "MonsterCharm", 0.25F, 0, 10, "Damage bonus against undead enemies for Emblem of Monster Slayer. Defined as percentage.");

        hostileDamageBonus = config.getFloat("HostileDamage", "MonsterCharm", 0.1F, 0, 10, "Damage bonus against agressive creatures for Emblem of Monster Slayer. Defined as percentage.");

        bonusLootingEnabled = config.getBoolean("BonusLooting", "MonsterCharm", true, "Whether or not Emblem of Monster Slayer should provide +1 Looting Level.");

        doubleXPEnabled = config.getBoolean("DoubleXP", "MonsterCharm", true, "Whether or not Emblem of Monster Slayer should provide double experience drop from monsters.");

        attackDamage = config.getFloat("DamageBoost", "Emblem of Bloodstained Valor", 1, 0, 100, "Damage increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.");

        attackSpeed = config.getFloat("AttackSpeedBoost", "Emblem of Bloodstained Valor", 1, 0, 100, "Attack speed increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.");

        movementSpeed = config.getFloat("SpeedBoost", "Emblem of Bloodstained Valor", 0.5F, 0, 100, "Movement speed increase provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.");

        damageResistance = config.getFloat("ResistanceBoost", "Emblem of Bloodstained Valor", 0.5F, 0, 100, "Damage resistance provided by Emblem of Bloodstained Valor for each missing percent of health. Measured as percentage.");

        radius = config.getInt("Radius", "Mega Sponge", 4, 1, 128, "Radius in which Exptrapolated Megaspong absorbs water. Default 4 equals to vanilla sponge");

        twistAttackDamage = config.getFloat("AttackDamage", "The Twist", 8, 0, 32768, "Attack damage of The Twist, actual damage shown in tooltip will be is 1 + this_value.");

        twistAttackSpeed = config.getFloat("AttackSpeed", "The Twist", -1.8F, -32768, 32768, "Attack speed of The Twist.");

        bossDamageBonus = config.getFloat("BossDamageBonus", "The Twist", 3, 0, 10, "Attack damage bonus of The Twist against players and bossess.");

        knockbackBonus = config.getFloat("KnockbackPowerBonus", "The Twist", 3, 0, 10, "Knockback bonus of The Twist. For Phantoms, this value is multiplied by 1.5.");

        xpCollectionRange = config.getFloat("CollectionRange", "Experience Scroll", 16.0F, 1, 128, "Range in which Scroll of Ageless Wisdom collects experience orbs when active.");

        //builder.pushCategory("Save the Bees", "This category exists solely because of Jusey1z who really wanted to protect his bees."
        //        + Configuration.NEW_LINE + "Btw Jusey, when I said 'very cute though', I meant you. Bees are cute either of course.");

        //saveTheBees = builder
        //        .comment("If true, bees will never affected by the Second Curse of Ring of the Seven Curses.")
        //        .getBoolean("DontTouchMyBees", false);

        // Ugly but gets the job done

        cursedScrollDamageBoost = config.getFloat("DamageBoost", "Cursed Scroll", 0.04F, 0, 1, "Damage increase provided by Scroll of a Thousand Curses for each curse, as percentage.");

        cursedScrollMiningBoost = config.getFloat("MiningBoost", "Cursed Scroll", 0.07F, 0, 1, "Mining speed increase provided by Scroll of a Thousand Curses for each curse, as percentage.");

        cursedScrollRegenBoost = config.getFloat("RegenBoost", "Cursed Scroll", 0.04F, 0, 1, "Health regeneration increase provided by Scroll of a Thousand Curses for each curse, as percentage.");

        infinitumAttackDamage = config.getInt("AttackDamage", "The Infinitum", 15, 0, 32768, "Attack damage of The Infinitum, actual damage shown in tooltip will be is 1 + this_value.");

        infinitumAttackSpeed = config.getFloat("AttackSpeed", "The Infinitum", -2.0F, -32768, 32768, "Attack speed of The Infinitum.");

        infinitumBossDamageBonus = config.getInt("BossDamageBonus", "The Infinitum", 2, 0, 10, "Attack damage bonus of The Infinitum against players and bosses.");

        infinitumKnockbackBonus = config.getFloat("KnockbackBonus", "The Infinitum", 2, 0, 10, "Knockback bonus of The Infinitum. For Phantoms, this value is multiplied by 1.5");

        infinitumLifestealBonus = config.getFloat("LifestealBonus", "The Infinitum", 0.1F, 0, 1, "Attack damage bonus of The Infinitum against players and bossess.");

        infinitumUndeadProbability = config.getFloat("UndeadProbability", "The Infinitum", 0.85F, 0, 1, "Knockback bonus of The Infinitum. For Phantoms, this value is multiplied by 1.5.");

        enigmaticAmuletDamageBonus = config.getFloat("DamageBonus", "Enigamtic Amulet", 1.5F, 0, 32768, "The damage bonus stat provided by red Enigmatic Amulet.");

        vesselEnabled = config.getBoolean("VesselEnabled", "Enigamtic Amulet", true, "Whether or not Enigmatic Amulet should be summoning Extradimensional Vessel on owner's death.");

        defaultArmorBonus = config.getFloat("DefaultArmor", "Golem Heart", 4.0F, 0, 256, "Default amount of armor points provided by Heart of the Golem.");

        superArmorBonus = config.getFloat("SuperArmor", "Golem Heart", 16.0F, 0, 256, "The amount of armor points provided by Heart of the Golem when it's bearer has no armor equipped.");

        superArmorToughnessBonus = config.getFloat("SuperArmorToughness", "Golem Heart", 4.0F, 0, 256, "The amount of armor toughness provided by Heart of the Golem when it's bearer has no armor equipped.");

        meleeResistance = config.getFloat("MeleeResistance", "Golem Heart", 0.25F, 0, 1, "Resistance to melee attacks provided by Heart of the Golem. Defined as percentage.");

        explosionResistance = config.getFloat("ExplosionResistance", "Golem Heart", 0.4F, 0, 1, "Resistance to explosion damage provided by Heart of the Golem. Defined as percentage.");

        knockbackResistance = config.getFloat("KnockbackResistance", "Golem Heart", 1F, 0, 1, "Resistance to knockback provided by Heart of the Golem. Defined as percentage.");

        vulnerabilityModifier = config.getFloat("VulnerabilityModifier", "Golem Heart", 2.0F, 1.0F, 256, "Modifier for Magic Damage vulnerability applied by Heart of the Golem. Default value of 2.0 means that player will receive twice as much damage from magic.");

        oceanStoneSpellstoneCooldown = config.getInt("Cooldown", "Ocean Stone", 600, 1, 25565, "Active ability cooldown for Will of the Ocean. Measured in ticks. 20 ticks equal to 1 second.");

        oceanStoneSwimmingSpeedBoost = config.getFloat("SwimBoost", "Ocean Stone", 2, 0, 10, "Swimming speed boost provided by Will of the Ocean. Defined as percentage.");

        oceanStoneUnderwaterCreaturesResistance = config.getFloat("UnderwaterCreaturesResistance", "Ocean Stone", 0.4F, 0, 1, "Damage resistance against underwater creatures provided by Will of the Ocean. Defined as percentage.");

        oceanStoneXpCostModifier = config.getFloat("UnderwaterCreaturesResistance", "Ocean Stone", 1.0F, 0, 1000, "Multiplier for experience consumption by active ability of Will of the Ocean.");


        neutralAngerBlacklist.clear();
        String[] blacklist = config.getStringList("CursedRingNeutralAngerBlacklist", "The Seven Curses", new String[]{"minecraft:ocelot", "minecraft:snowman", "lycanitesmobs:arisaur", "lycanitesmobs:aspid", "lycanitesmobs:aegis", "lycanitesmobs:nymph", "lycanitesmobs:wisp", "lycanitesmobs:silex", "lycanitesmobs:yale", "lycanitesmobs:bobeko", "lycanitesmobs:maka"}, "List of entities that should never be affected"
                + " by the Second Curse of Ring of the Seven Curses. Examples: minecraft:villager_golem, minecraft:wolf. Changing this option required game restart to take effect.");

        Arrays.stream(blacklist).forEach(entry -> neutralAngerBlacklist.add(new ResourceLocation(entry)));

        neutralAngerWhitelist.clear();
        String[] whitelist = config.getStringList("CursedRingNeutralAngerBlacklist", "The Seven Curses", new String[]{"minecraft:wolf", "minecraft:villager_golem"}, "List of entities that should be affected"
                + " by the Second Curse of Ring of the Seven Curses. Examples: minecraft:villager_golem, minecraft:wolf. Changing this option required game restart to take effect. Need enable 'enableWhitelist' to work.");

        Arrays.stream(whitelist).forEach(entry -> neutralAngerWhitelist.add(new ResourceLocation(entry)));

        cursedItemList.clear();
        String[] cursed = config.getStringList("ItemBeCursed", "The Seven Curses", new String[]{"enigmaticlegacy:twisted_core", "enigmaticlegacy:the_twist", "enigmaticlegacy:berserk_emblem", "enigmaticlegacy:evil_essence", "enigmaticlegacy:enchanter_pearl", "enigmaticlegacy:cursed_scroll", "enigmaticlegacy:infernal_shield"}, "List of items needs ware ring to use"
                + "Examples: minecraft:dirt, minecraft:diamond_sword. Changing this option required game restart to take effect.");

        Arrays.stream(cursed).forEach(entry -> cursedItemList.add(new ResourceLocation(entry)));

        eldritchItemList.clear();
        String[] eldritch = config.getStringList("ItemBeDeeplyCursed", "The Seven Curses", new String[]{"enigmaticlegacy:abyssal_heart", "enigmaticlegacy:the_infinitum"}, "List of items needs ware ring during gameplay 99.5% times to use"
                + "Examples: minecraft:dirt, minecraft:diamond_sword. Changing this option required game restart to take effect.");

        Arrays.stream(eldritch).forEach(entry -> eldritchItemList.add(new ResourceLocation(entry)));


        config.save();
    }

}
